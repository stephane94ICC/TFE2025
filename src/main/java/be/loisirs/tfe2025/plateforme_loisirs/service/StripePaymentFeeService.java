package be.loisirs.tfe2025.plateforme_loisirs.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.ApplicationFee;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.FeeRefundCollectionCreateParams;
import com.stripe.param.PaymentIntentRetrieveParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;


@Service
public class StripePaymentFeeService {

    private static final Logger log = LoggerFactory.getLogger(StripePaymentFeeService.class);

    private final String stripeSecretKey;

    public StripePaymentFeeService(@Value("${stripe.secret-key}") String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
    }

    /**
     * Frais réels prélevés par Stripe sur ce paiement, et ce que la plateforme retient encore.
     * Vide si Stripe ne fournit pas ces informations (paiement sans retenue, par exemple).
     */
    public Optional<PaymentFees> findFees(String paymentIntentId) {
        try {
            Stripe.apiKey = stripeSecretKey;

            PaymentIntent paymentIntent = PaymentIntent.retrieve(
                    paymentIntentId,
                    PaymentIntentRetrieveParams.builder()
                            .addExpand("latest_charge.balance_transaction")
                            .addExpand("latest_charge.application_fee")
                            .build(),
                    null
            );

            Charge charge = paymentIntent.getLatestChargeObject();

            if (charge == null
                    || charge.getBalanceTransactionObject() == null
                    || charge.getApplicationFeeObject() == null) {
                return Optional.empty();
            }

            ApplicationFee applicationFee = charge.getApplicationFeeObject();
            long retained = applicationFee.getAmount()
                    - Objects.requireNonNullElse(applicationFee.getAmountRefunded(), 0L);

            return Optional.of(new PaymentFees(
                    applicationFee.getId(),
                    toEuros(charge.getBalanceTransactionObject().getFee()),
                    toEuros(retained)
            ));

        } catch (StripeException exception) {
            log.error("Stripe n'a pas donné les frais du paiement {} : {}",
                    paymentIntentId, exception.getMessage());
            throw new IllegalStateException("Impossible de lire les frais du paiement chez Stripe.", exception);
        }
    }

    /**
     * Rend au partenaire une partie de la retenue de la plateforme. La clé d'idempotence
     * empêche Stripe d'exécuter deux fois la même demande.
     */
    public void returnToPartner(String applicationFeeId, BigDecimal amount, String idempotencyKey) {
        try {
            Stripe.apiKey = stripeSecretKey;

            ApplicationFee applicationFee = ApplicationFee.retrieve(applicationFeeId);

            applicationFee.getRefunds().create(
                    FeeRefundCollectionCreateParams.builder()
                            .setAmount(toCents(amount))
                            .build(),
                    RequestOptions.builder()
                            .setIdempotencyKey(idempotencyKey)
                            .build()
            );

        } catch (StripeException exception) {
            log.error("Stripe a refusé de rendre {} EUR sur la retenue {} : {}",
                    amount, applicationFeeId, exception.getMessage());
            throw new IllegalStateException("Impossible de rendre une partie de la retenue au partenaire.", exception);
        }
    }

    private static BigDecimal toEuros(long cents) {
        return BigDecimal.valueOf(cents, 2);
    }

    private static long toCents(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    /**
     * @param applicationFeeId identifiant Stripe de la retenue de la plateforme
     * @param stripeFee        frais réels prélevés par Stripe, en euros
     * @param currentRetention ce que la plateforme retient encore, en euros
     */
    public record PaymentFees(String applicationFeeId, BigDecimal stripeFee, BigDecimal currentRetention) {
    }
}