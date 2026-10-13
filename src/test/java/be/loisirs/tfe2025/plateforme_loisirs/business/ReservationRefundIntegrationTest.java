package be.loisirs.tfe2025.plateforme_loisirs.business;

import be.loisirs.tfe2025.plateforme_loisirs.common.AbstractIntegrationTest;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.StripePaymentFeeService.PaymentFees;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Annulation = remboursement.
 *
 * Le composant Stripe est un faux (voir AbstractIntegrationTest) : ces tests
 * verifient la logique de l'application (quand rembourser, avec quels
 * parametres, dans quel ordre), pas le reseau. Le vrai remboursement est
 * verifie manuellement en mode test Stripe.
 */
@DisplayName("Remboursement à l'annulation")
class ReservationRefundIntegrationTest extends AbstractIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Sans payment intent : 409 REFUND_NOT_AVAILABLE, aucun appel à Stripe, réservation intacte")
    void reservationWithoutPaymentIntentIsNotCancelled() throws Exception {
        User member = createMember("refund-no-pi@belloisirs.test");
        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(member, session);
        reservation.setStripePaymentIntentId(null);
        reservationRepository.save(reservation);

        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(tokenFor(member.getEmail()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REFUND_NOT_AVAILABLE"));

        // On n'annule jamais une vente payee sans la rembourser.
        verify(stripeRefundService, never()).refund(anyString(), anyLong(), anyBoolean(), anyBoolean());

        entityManager.flush();
        entityManager.clear();

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(after.getCancelledAt()).isNull();
        assertThat(after.getStripeRefundId()).isNull();
    }

    @Test
    @DisplayName("Vente Connect, retenue inconnue : reprise au partenaire, Stripe lui rend toute la retenue")
    void connectSaleIsRefundedWithReverseTransfer() throws Exception {
        // Le faux des frais renvoie un Optional vide par defaut : retenue inconnue.
        User member = createMember("refund-connect@belloisirs.test");
        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(member, session);
        reservation.setCommissionRate(new BigDecimal("5.00"));
        reservation.setCommissionHtva(new BigDecimal("1.03"));
        reservation.setCommissionVat(new BigDecimal("0.22"));
        reservationRepository.save(reservation);

        String paymentIntentId = reservation.getStripePaymentIntentId();

        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(tokenFor(member.getEmail()))))
                .andExpect(status().isNoContent());

        verify(stripeRefundService).refund(eq(paymentIntentId), eq(reservation.getId()), eq(true), eq(true));
        verify(stripePaymentFeeService, never()).returnToPartner(any(), any(), any());

        // Ecriture forcee en base : les CHECK de V14_3 sont reellement evalues par MySQL.
        entityManager.flush();
        entityManager.clear();

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(after.getStripeRefundId()).startsWith("re_test_");
        assertThat(after.getRefundedAt()).isNotNull();
        assertThat(after.getCancelledAt()).isEqualTo(after.getRefundedAt());
    }

    @Test
    @DisplayName("Vente antérieure à Connect (sans commission) : remboursement simple")
    void saleWithoutCommissionIsRefundedWithoutReverseTransfer() throws Exception {
        User member = createMember("refund-legacy@belloisirs.test");
        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(member, session);

        String paymentIntentId = reservation.getStripePaymentIntentId();

        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(tokenFor(member.getEmail()))))
                .andExpect(status().isNoContent());

        verify(stripeRefundService).refund(eq(paymentIntentId), eq(reservation.getId()), eq(false), eq(false));
        // Sans paiement partage, aucune retenue a regler.
        verifyNoInteractions(stripePaymentFeeService);

        entityManager.flush();
        entityManager.clear();

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(after.getStripeRefundId()).startsWith("re_test_");
    }

    @Test
    @DisplayName("Vente avec frais : le partenaire garde les frais (0,63 €), la plateforme lui rend sa commission (0,90 €)")
    void partnerKeepsPaymentFeeAndGetsCommissionBack() throws Exception {
        User member = createMember("refund-fee@belloisirs.test");
        Reservation reservation = connectSale(member, true);
        String paymentIntentId = reservation.getStripePaymentIntentId();

        // Retenue apres l'ajustement d'une carte belge : 1,89 - 0,36 = 1,53 EUR.
        when(stripePaymentFeeService.findFees(paymentIntentId))
                .thenReturn(Optional.of(new PaymentFees("fee_refund_1", new BigDecimal("0.52"), new BigDecimal("1.53"))));

        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(tokenFor(member.getEmail()))))
                .andExpect(status().isNoContent());

        // Stripe ne rend pas la retenue : la plateforme rend elle-meme la commission seule.
        verify(stripeRefundService).refund(eq(paymentIntentId), eq(reservation.getId()), eq(true), eq(false));
        verify(stripePaymentFeeService).returnToPartner(
                "fee_refund_1", new BigDecimal("0.90"), "refund-retention-res-" + reservation.getId());

        entityManager.flush();
        entityManager.clear();

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(after.getRefundedAt()).isNotNull();
    }

    @Test
    @DisplayName("Vente Connect antérieure aux frais : toute la retenue (la commission) est rendue, comme avant")
    void olderConnectSaleGetsWholeRetentionBack() throws Exception {
        User member = createMember("refund-old-connect@belloisirs.test");
        Reservation reservation = connectSale(member, false);
        String paymentIntentId = reservation.getStripePaymentIntentId();

        when(stripePaymentFeeService.findFees(paymentIntentId))
                .thenReturn(Optional.of(new PaymentFees("fee_refund_2", new BigDecimal("0.52"), new BigDecimal("0.90"))));

        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(tokenFor(member.getEmail()))))
                .andExpect(status().isNoContent());

        verify(stripeRefundService).refund(eq(paymentIntentId), eq(reservation.getId()), eq(true), eq(false));
        verify(stripePaymentFeeService).returnToPartner(
                "fee_refund_2", new BigDecimal("0.90"), "refund-retention-res-" + reservation.getId());
    }

    /**
     * Vente de 18 EUR partagee avec le partenaire, commission 0,74 + 0,16 = 0,90 EUR.
     * Avec frais : frais reels d'une carte belge deja enregistres, 0,52 + 0,11 = 0,63 EUR.
     */
    private Reservation connectSale(User member, boolean withPaymentFee) {
        Reservation reservation = createConfirmedReservation(member, createUpcomingSession());
        reservation.setTotalPrice(new BigDecimal("18.00"));
        reservation.setCommissionRate(new BigDecimal("5.00"));
        reservation.setCommissionHtva(new BigDecimal("0.74"));
        reservation.setCommissionVat(new BigDecimal("0.16"));

        if (withPaymentFee) {
            reservation.setPaymentFeeHtva(new BigDecimal("0.52"));
            reservation.setPaymentFeeVat(new BigDecimal("0.11"));
        }

        return reservationRepository.save(reservation);
    }

    @Test
    @DisplayName("Liste du membre : refundable vrai avec paiement Stripe, faux sans, identifiant Stripe jamais exposé")
    void reservationListExposesRefundableFlagOnly() throws Exception {
        User member = createMember("refund-flag@belloisirs.test");
        ActivitySession session = createUpcomingSession();

        Reservation paid = createConfirmedReservation(member, session);

        Reservation legacy = createConfirmedReservation(member, session);
        legacy.setStripePaymentIntentId(null);
        reservationRepository.save(legacy);

        mockMvc.perform(get("/api/member/reservations")
                        .header(HttpHeaders.AUTHORIZATION, bearer(tokenFor(member.getEmail()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.reference == '" + paid.getReference() + "')].refundable")
                        .value(true))
                .andExpect(jsonPath("$[?(@.reference == '" + legacy.getReference() + "')].refundable")
                        .value(false))
                // Le front recoit une decision, jamais l'identifiant Stripe.
                .andExpect(jsonPath("$[0].stripePaymentIntentId").doesNotExist());
    }
}