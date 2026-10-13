package be.loisirs.tfe2025.plateforme_loisirs.business;

import be.loisirs.tfe2025.plateforme_loisirs.util.PaymentFeeCalculator;
import be.loisirs.tfe2025.plateforme_loisirs.util.PaymentFeeCalculator.FeeAdjustment;
import be.loisirs.tfe2025.plateforme_loisirs.util.PaymentFeeCalculator.FeeBreakdown;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires purs, comme CommissionCalculatorTest : aucun contexte Spring, aucune base,
 * aucun appel Stripe. Les montants sont comparés avec leur échelle (2 décimales).
 *
 * Exemple suivi : vente de 18 € TVAC, commission 5 % = 0,90 € TVAC (0,74 + 0,16).
 * Frais maximaux, ceux des cartes internationales : 3,15 % + 0,25 €.
 * Retenue au paiement : 0,90 de commission + 0,99 de frais maximaux TVAC = 1,89 €.
 */
class PaymentFeeCalculatorTest {

    private static final BigDecimal MAX_PERCENT = bd("3.15");
    private static final BigDecimal MAX_FIXED = bd("0.25");
    private static final BigDecimal COMMISSION_TOTAL = bd("0.90");
    private static final BigDecimal MAX_FEE = bd("0.82");
    private static final BigDecimal RETENTION = bd("1.89");

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private static void assertFee(FeeBreakdown fee, String htva, String vat, String total) {
        assertThat(fee.feeHtva()).isEqualTo(bd(htva));
        assertThat(fee.feeVat()).isEqualTo(bd(vat));
        assertThat(fee.total()).isEqualTo(bd(total));
    }

    // ---------- Frais maximaux ----------

    @Test
    @DisplayName("18 € à 3,15 % + 0,25 € → 0,82 € (0,817 arrondi au cent)")
    void maxFeeOfTestSale() {
        Assertions.assertThat( PaymentFeeCalculator.maxFee(bd("18.00"), MAX_PERCENT, MAX_FIXED)).isEqualTo(MAX_FEE);
    }

    @Test
    @DisplayName("Arrondi half-up : 50 € à 3,15 % + 0,25 € = 1,825 → 1,83")
    void maxFeeRoundsHalfUp() {
        assertThat(PaymentFeeCalculator.maxFee(bd("50.00"), MAX_PERCENT, MAX_FIXED)).isEqualTo(bd("1.83"));
    }

    @Test
    @DisplayName("Carte belge standard : 18 € à 1,5 % + 0,25 € → 0,52 €")
    void feeOfStandardCard() {
        assertThat(PaymentFeeCalculator.maxFee(bd("18.00"), bd("1.50"), MAX_FIXED)).isEqualTo(bd("0.52"));
    }

    // ---------- TVA de la plateforme sur les frais ----------

    @Test
    @DisplayName("TVA 21 % : 0,82 → 0,17 de TVA → 0,99 ; 0,52 → 0,11 de TVA → 0,63")
    void feeWithPlatformVat() {
        assertFee(PaymentFeeCalculator.withVat(MAX_FEE), "0.82", "0.17", "0.99");
        assertFee(PaymentFeeCalculator.withVat(bd("0.52")), "0.52", "0.11", "0.63");
    }

    // ---------- Ajustement aux frais réels ----------

    @Test
    @DisplayName("Carte belge (frais réels 0,52 €) : le partenaire paie 0,63 €, on lui rend 0,36 €")
    void adjustsToRealFee() {
        FeeAdjustment adjustment = PaymentFeeCalculator.adjust(RETENTION, COMMISSION_TOTAL, MAX_FEE, bd("0.52"));

        assertFee(adjustment.partnerFee(), "0.52", "0.11", "0.63");
        assertThat(adjustment.amountToReturn()).isEqualTo(bd("0.36"));
    }

    @Test
    @DisplayName("Carte américaine (frais réels = maximum) : rien à rendre")
    void nothingToReturnAtMaximum() {
        FeeAdjustment adjustment = PaymentFeeCalculator.adjust(RETENTION, COMMISSION_TOTAL, MAX_FEE, MAX_FEE);

        assertFee(adjustment.partnerFee(), "0.82", "0.17", "0.99");
        assertThat(adjustment.amountToReturn()).isEqualTo(bd("0.00"));
    }

    @Test
    @DisplayName("Frais réels au-dessus du maximum : le partenaire paie le maximum, la plateforme l'écart")
    void partnerNeverPaysMoreThanMaximum() {
        FeeAdjustment adjustment = PaymentFeeCalculator.adjust(RETENTION, COMMISSION_TOTAL, MAX_FEE, bd("1.20"));

        assertFee(adjustment.partnerFee(), "0.82", "0.17", "0.99");
        assertThat(adjustment.amountToReturn()).isEqualTo(bd("0.00"));
    }

    @Test
    @DisplayName("Notification reçue deux fois : la différence déjà rendue ne l'est pas une seconde fois")
    void secondNotificationReturnsNothing() {
        // Après le premier ajustement, la retenue est passée de 1,89 à 1,53 €.
        FeeAdjustment adjustment = PaymentFeeCalculator.adjust(bd("1.53"), COMMISSION_TOTAL, MAX_FEE, bd("0.52"));

        assertThat(adjustment.amountToReturn()).isEqualTo(bd("0.00"));
    }

    // ---------- Remboursement de la réservation ----------

    @Test
    @DisplayName("Remboursement : la plateforme garde les frais (0,63 €) et rend sa commission (0,90 €)")
    void refundReturnsCommissionOnly() {
        // Retenue après l'ajustement d'une carte belge : 1,89 - 0,36 = 1,53 €.
        assertThat(PaymentFeeCalculator.amountToReturnOnRefund(bd("1.53"), bd("0.63"))).isEqualTo(bd("0.90"));
    }

    @Test
    @DisplayName("Vente antérieure aux frais (frais du partenaire à zéro) : toute la retenue est rendue, comme avant")
    void refundOfOlderSaleReturnsWholeRetention() {
        assertThat(PaymentFeeCalculator.amountToReturnOnRefund(COMMISSION_TOTAL, bd("0.00"))).isEqualTo(bd("0.90"));
    }

    @Test
    @DisplayName("Seconde tentative de remboursement : la commission déjà rendue ne l'est pas une seconde fois")
    void secondRefundAttemptReturnsNothing() {
        // La commission a déjà été rendue : il ne reste que les frais du partenaire.
        assertThat(PaymentFeeCalculator.amountToReturnOnRefund(bd("0.63"), bd("0.63"))).isEqualTo(bd("0.00"));
    }

    // ---------- Refus ----------

    @Test
    @DisplayName("Refus : montant absent ou négatif")
    void rejectsMissingOrNegativeAmounts() {
        assertThatThrownBy(() -> PaymentFeeCalculator.maxFee(null, MAX_PERCENT, MAX_FIXED))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PaymentFeeCalculator.maxFee(bd("18.00"), MAX_PERCENT, bd("-0.01")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PaymentFeeCalculator.withVat(bd("-0.01")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PaymentFeeCalculator.adjust(RETENTION, COMMISSION_TOTAL, MAX_FEE, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PaymentFeeCalculator.amountToReturnOnRefund(bd("-0.01"), bd("0.63")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Refus : pourcentage absent, négatif ou au-dessus de 10 % (erreur de configuration)")
    void rejectsPercentOutOfRange() {
        assertThatThrownBy(() -> PaymentFeeCalculator.maxFee(bd("18.00"), null, MAX_FIXED))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PaymentFeeCalculator.maxFee(bd("18.00"), bd("-0.01"), MAX_FIXED))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PaymentFeeCalculator.maxFee(bd("18.00"), bd("10.01"), MAX_FIXED))
                .isInstanceOf(IllegalArgumentException.class);
    }
}