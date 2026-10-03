package be.loisirs.tfe2025.plateforme_loisirs.util;

import be.loisirs.tfe2025.plateforme_loisirs.util.CommissionCalculator.CommissionBreakdown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires purs : aucun contexte Spring, aucune base, aucun appel Stripe.
 * Les montants sont comparés avec leur échelle (2 décimales) : 2.5 n'est pas accepté pour 2.50.
 */
class CommissionCalculatorTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private static void assertBreakdown(CommissionBreakdown result,
                                        String saleHtva,
                                        String commissionHtva,
                                        String commissionVat,
                                        String commissionTotal) {
        assertThat(result.saleHtva()).isEqualTo(bd(saleHtva));
        assertThat(result.commissionHtva()).isEqualTo(bd(commissionHtva));
        assertThat(result.commissionVat()).isEqualTo(bd(commissionVat));
        assertThat(result.commissionTotal()).isEqualTo(bd(commissionTotal));
    }

    // ---------- Cas nominaux ----------

    @Test
    @DisplayName("50 € TVAC à 21 %, commission 5 % → 41,32 / 2,07 / 0,43 / 2,50")
    void activityAt21PercentVat() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("50.00"), bd("21.00"), bd("5.00"));

        assertBreakdown(result, "41.32", "2.07", "0.43", "2.50");
        assertThat(result.commissionRate()).isEqualTo(bd("5.00"));
    }

    @Test
    @DisplayName("32 € TVAC à 6 %, commission 5 % → 30,19 / 1,51 / 0,32 / 1,83")
    void activityAt6PercentVat() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("32.00"), bd("6.00"), bd("5.00"));

        assertBreakdown(result, "30.19", "1.51", "0.32", "1.83");
    }

    @Test
    @DisplayName("TVA de la vente à 0 % : HTVA = TTC, la commission garde sa TVA à 21 %")
    void activityAt0PercentVat() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("40.00"), bd("0.00"), bd("8.00"));

        assertBreakdown(result, "40.00", "3.20", "0.67", "3.87");
    }

    @Test
    @DisplayName("Arrondi half-up : 2,065 devient 2,07 (et non 2,06 comme en arrondi bancaire)")
    void roundsHalfUp() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("41.30"), bd("0.00"), bd("5.00"));

        assertBreakdown(result, "41.30", "2.07", "0.43", "2.50");
    }

    @Test
    @DisplayName("Borne basse acceptée : commission à 1 %")
    void acceptsMinimumCommissionRate() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("50.00"), bd("21.00"), bd("1.00"));

        assertBreakdown(result, "41.32", "0.41", "0.09", "0.50");
    }

    @Test
    @DisplayName("Borne haute acceptée : commission à 8 %")
    void acceptsMaximumCommissionRate() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("50.00"), bd("21.00"), bd("8.00"));

        assertBreakdown(result, "41.32", "3.31", "0.70", "4.01");
    }

    @Test
    @DisplayName("Prix nul : commission nulle, aucune erreur")
    void zeroPriceGivesZeroCommission() {
        CommissionBreakdown result = CommissionCalculator.calculate(bd("0.00"), bd("21.00"), bd("5.00"));

        assertBreakdown(result, "0.00", "0.00", "0.00", "0.00");
    }

    // ---------- Refus ----------

    @Test
    @DisplayName("Refus : prix, taux de TVA ou taux de commission absent")
    void rejectsNullArguments() {
        assertThatThrownBy(() -> CommissionCalculator.calculate(null, bd("21.00"), bd("5.00")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CommissionCalculator.calculate(bd("50.00"), null, bd("5.00")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CommissionCalculator.calculate(bd("50.00"), bd("21.00"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Refus : prix négatif")
    void rejectsNegativePrice() {
        assertThatThrownBy(() -> CommissionCalculator.calculate(bd("-0.01"), bd("21.00"), bd("5.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Refus : taux de TVA non autorisé en Belgique (10 %)")
    void rejectsUnknownVatRate() {
        assertThatThrownBy(() -> CommissionCalculator.calculate(bd("50.00"), bd("10.00"), bd("5.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Refus : commission sous 1 %")
    void rejectsCommissionBelowMinimum() {
        assertThatThrownBy(() -> CommissionCalculator.calculate(bd("50.00"), bd("21.00"), bd("0.99")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Refus : commission au-dessus de 8 %")
    void rejectsCommissionAboveMaximum() {
        assertThatThrownBy(() -> CommissionCalculator.calculate(bd("50.00"), bd("21.00"), bd("8.01")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}