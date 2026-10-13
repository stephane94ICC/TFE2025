package be.loisirs.tfe2025.plateforme_loisirs.business;

import be.loisirs.tfe2025.plateforme_loisirs.common.AbstractIntegrationTest;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.StripePaymentFeeService.PaymentFees;
import com.stripe.net.Webhook;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Confirmation d'un paiement par le webhook, avec ajustement des frais Stripe.
 *
 * Les événements sont signés ici avec le secret de test, comme Stripe le ferait :
 * c'est le vrai chemin (contrôleur, vérification de signature, service) qui est testé.
 * Les appels à Stripe passent par le faux StripePaymentFeeService du socle.
 *
 * Exemple suivi : vente de 18 € TVAC, commission 0,74 + 0,16 = 0,90 €, frais maximaux
 * 0,82 + 0,17 = 0,99 €. Retenue au paiement : 1,89 €.
 */
@DisplayName("Webhook Stripe : frais de paiement réels à la confirmation")
class StripeWebhookPaymentFeeIntegrationTest extends AbstractIntegrationTest {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Carte belge : frais réels 0,52 €, le partenaire paie 0,63 € et on lui rend 0,36 €")
    void returnsDifferenceToPartner() throws Exception {
        Reservation sale = pendingSale(true);
        String paymentIntentId = "pi_test_" + sale.getId();

        when(stripePaymentFeeService.findFees(paymentIntentId))
                .thenReturn(Optional.of(new PaymentFees("fee_test_1", bd("0.52"), bd("1.89"))));

        sendCompletedEvent(sale.getStripeSessionId(), paymentIntentId);

        Reservation confirmed = reload(sale);
        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(confirmed.getStripePaymentIntentId()).isEqualTo(paymentIntentId);
        assertThat(confirmed.getPaymentFeeHtva()).isEqualTo(bd("0.52"));
        assertThat(confirmed.getPaymentFeeVat()).isEqualTo(bd("0.11"));
        assertThat(confirmed.getBillingCity()).isEqualTo("Ixelles");

        verify(stripePaymentFeeService)
                .returnToPartner("fee_test_1", bd("0.36"), "fee-adjustment-res-" + sale.getId());
    }

    @Test
    @DisplayName("Carte américaine : frais réels égaux au maximum, rien n'est rendu")
    void nothingToReturnWhenRealFeeIsMaximum() throws Exception {
        Reservation sale = pendingSale(true);
        String paymentIntentId = "pi_test_" + sale.getId();

        when(stripePaymentFeeService.findFees(paymentIntentId))
                .thenReturn(Optional.of(new PaymentFees("fee_test_2", bd("0.82"), bd("1.89"))));

        sendCompletedEvent(sale.getStripeSessionId(), paymentIntentId);

        Reservation confirmed = reload(sale);
        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(confirmed.getPaymentFeeHtva()).isEqualTo(bd("0.82"));
        assertThat(confirmed.getPaymentFeeVat()).isEqualTo(bd("0.17"));

        verify(stripePaymentFeeService, never()).returnToPartner(any(), any(), any());
    }

    @Test
    @DisplayName("Frais réels indisponibles : la réservation est confirmée, les frais maximaux restent")
    void keepsMaximumWhenFeesAreUnavailable() throws Exception {
        // Le faux renvoie un Optional vide par défaut : Stripe n'a rien fourni.
        Reservation sale = pendingSale(true);

        sendCompletedEvent(sale.getStripeSessionId(), "pi_test_" + sale.getId());

        Reservation confirmed = reload(sale);
        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(confirmed.getPaymentFeeHtva()).isEqualTo(bd("0.82"));
        assertThat(confirmed.getPaymentFeeVat()).isEqualTo(bd("0.17"));

        verify(stripePaymentFeeService, never()).returnToPartner(any(), any(), any());
    }

    @Test
    @DisplayName("Vente antérieure aux frais : confirmée sans aucun appel des frais à Stripe")
    void olderSaleIsConfirmedWithoutFeeAdjustment() throws Exception {
        Reservation sale = pendingSale(false);

        sendCompletedEvent(sale.getStripeSessionId(), "pi_test_" + sale.getId());

        Reservation confirmed = reload(sale);
        assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(confirmed.getPaymentFeeHtva()).isNull();
        assertThat(confirmed.getPaymentFeeVat()).isNull();

        verifyNoInteractions(stripePaymentFeeService);
    }

    // ---------- Outils ----------

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    /**
     * Réservation en attente de paiement, telle que la création du paiement l'enregistre.
     * Avec frais : les frais maximaux (3,15 % + 0,25 € sur 18 €) sont déjà notés.
     */
    private Reservation pendingSale(boolean withPaymentFee) {
        User member = createMember("frais-" + UUID.randomUUID() + "@belloisirs.test");

        Reservation reservation = new Reservation();
        reservation.setUser(member);
        reservation.setSession(createUpcomingSession());
        reservation.setQuantity(1);
        reservation.setTotalPrice(bd("18.00"));
        reservation.setVatRate(bd("21.00"));
        reservation.setCommissionRate(bd("5.00"));
        reservation.setCommissionHtva(bd("0.74"));
        reservation.setCommissionVat(bd("0.16"));

        if (withPaymentFee) {
            reservation.setPaymentFeeHtva(bd("0.82"));
            reservation.setPaymentFeeVat(bd("0.17"));
        }

        reservation.setReference("TEST-" + UUID.randomUUID().toString().substring(0, 8));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setStripeSessionId("cs_test_" + UUID.randomUUID());
        reservation.setBillingFirstName(member.getFirstName());
        reservation.setBillingLastName(member.getLastName());
        reservation.setBillingEmail(member.getEmail());

        return reservationRepository.save(reservation);
    }

    /** Envoie l'événement « paiement terminé » signé avec le secret de test, comme Stripe. */
    private void sendCompletedEvent(String stripeSessionId, String paymentIntentId) throws Exception {
        String payload = """
                {
                  "id": "evt_test_%s",
                  "object": "event",
                  "type": "checkout.session.completed",
                  "data": {
                    "object": {
                      "id": "%s",
                      "object": "checkout.session",
                      "payment_status": "paid",
                      "payment_intent": "%s",
                      "customer_details": {
                        "address": {
                          "line1": "Avenue Louise 54",
                          "postal_code": "1050",
                          "city": "Ixelles",
                          "country": "BE"
                        }
                      }
                    }
                  }
                }
                """.formatted(UUID.randomUUID(), stripeSessionId, paymentIntentId);

        long timestamp = Webhook.Util.getTimeNow();
        String signature = Webhook.Util.computeHmacSha256(webhookSecret, timestamp + "." + payload);

        mockMvc.perform(post("/api/stripe/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=" + timestamp + ",v1=" + signature)
                        .content(payload))
                .andExpect(status().isOk());
    }

    /** Relit la réservation en base, et non dans le cache de l'ORM. */
    private Reservation reload(Reservation reservation) {
        entityManager.flush();
        entityManager.clear();

        return reservationRepository.findById(reservation.getId()).orElseThrow();
    }
}