package be.loisirs.tfe2025.plateforme_loisirs.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.RefundCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeRefundService {

    private static final Logger log = LoggerFactory.getLogger(StripeRefundService.class);

    private final String stripeSecretKey;

    public StripeRefundService(@Value("${stripe.secret-key}") String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
    }

    /**
     * Rembourse le client en entier.
     *
     * @param connectSale          paiement partagé avec un partenaire : le virement qui lui
     *                             a été fait est repris (reverse_transfer)
     * @param refundApplicationFee Stripe rend aussi au partenaire toute la retenue de la
     *                             plateforme. Faux d'ordinaire : la plateforme ne lui rend que
     *                             sa commission (MemberReservationService), les frais de
     *                             paiement restent à sa charge.
     */
    public String refund(String paymentIntentId,
                         Long reservationId,
                         boolean connectSale,
                         boolean refundApplicationFee) {
        try {
            Stripe.apiKey = stripeSecretKey;

            RefundCreateParams.Builder params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .putMetadata("reservationId", reservationId.toString());

            if (connectSale) {
                params.setReverseTransfer(true)
                        .setRefundApplicationFee(refundApplicationFee);
            }

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey("refund-res-" + reservationId)
                    .build();

            return Refund.create(params.build(), options).getId();

        } catch (StripeException exception) {
            // Le message de Stripe est conservé dans les logs : sans lui, impossible de diagnostiquer
            log.error("Stripe a refusé le remboursement de la réservation {} : {}",
                    reservationId, exception.getMessage());
            throw new IllegalStateException("Erreur lors du remboursement Stripe.", exception);
        }
    }
}