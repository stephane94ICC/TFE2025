package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        verify(stripeRefundService, never()).refund(anyString(), anyLong(), anyBoolean());

        entityManager.flush();
        entityManager.clear();

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(after.getCancelledAt()).isNull();
        assertThat(after.getStripeRefundId()).isNull();
    }

    @Test
    @DisplayName("Vente Connect (commission figée) : remboursement avec reprise au partenaire, colonnes remplies")
    void connectSaleIsRefundedWithReverseTransfer() throws Exception {
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

        verify(stripeRefundService).refund(eq(paymentIntentId), eq(reservation.getId()), eq(true));

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

        verify(stripeRefundService).refund(eq(paymentIntentId), eq(reservation.getId()), eq(false));

        entityManager.flush();
        entityManager.clear();

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(after.getStripeRefundId()).startsWith("re_test_");
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