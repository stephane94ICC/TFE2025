package be.loisirs.tfe2025.plateforme_loisirs.util;

import java.math.BigDecimal;
import java.math.RoundingMode;


public final class PaymentFeeCalculator {

    /** Garde-fou contre une erreur de configuration : aucun tarif Stripe n'approche 10 %. */
    private static final BigDecimal MAX_PERCENT = new BigDecimal("10.00");

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private PaymentFeeCalculator() {
    }

    /**
     * Frais maximaux HTVA d'un paiement : pourcentage du montant payé, plus la partie fixe,
     * arrondis au cent. Exemple : 18 € à 3,15 % + 0,25 € = 0,817, soit 0,82 €.
     */
    public static BigDecimal maxFee(BigDecimal amountPaid, BigDecimal percent, BigDecimal fixedFee) {
        requireAmount(amountPaid, "montant payé");
        requireAmount(fixedFee, "partie fixe des frais");

        if (percent == null || percent.signum() < 0 || percent.compareTo(MAX_PERCENT) > 0) {
            throw new IllegalArgumentException("Pourcentage de frais hors limites (0 à 10 %) : " + percent);
        }

        return amountPaid
                .multiply(percent.movePointLeft(2))
                .add(fixedFee)
                .setScale(SCALE, ROUNDING);
    }

    /** Ajoute aux frais la TVA de la plateforme, comme pour la commission. */
    public static FeeBreakdown withVat(BigDecimal feeHtva) {
        requireAmount(feeHtva, "frais");

        BigDecimal htva = feeHtva.setScale(SCALE, ROUNDING);
        BigDecimal vat = htva
                .multiply(CommissionCalculator.PLATFORM_VAT_RATE.movePointLeft(2))
                .setScale(SCALE, ROUNDING);

        return new FeeBreakdown(htva, vat, htva.add(vat));
    }


    public static FeeAdjustment adjust(BigDecimal currentRetention,
                                       BigDecimal commissionTotal,
                                       BigDecimal maxFeeHtva,
                                       BigDecimal realFeeHtva) {
        requireAmount(currentRetention, "retenue");
        requireAmount(commissionTotal, "commission");
        requireAmount(maxFeeHtva, "frais maximaux");
        requireAmount(realFeeHtva, "frais réels");

        FeeBreakdown partnerFee = withVat(realFeeHtva.min(maxFeeHtva));

        BigDecimal amountToReturn = currentRetention
                .subtract(commissionTotal)
                .subtract(partnerFee.total())
                .max(BigDecimal.ZERO)
                .setScale(SCALE, ROUNDING);

        return new FeeAdjustment(partnerFee, amountToReturn);
    }


    public static BigDecimal commissionToReturn(BigDecimal currentRetention, BigDecimal commissionTotal) {
        requireAmount(currentRetention, "retenue");
        requireAmount(commissionTotal, "commission");

        return commissionTotal.min(currentRetention).setScale(SCALE, ROUNDING);
    }

    private static void requireAmount(BigDecimal amount, String label) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Montant absent ou négatif (" + label + ") : " + amount);
        }
    }

    /** Frais avec la TVA de la plateforme : HTVA, TVA et total. */
    public record FeeBreakdown(BigDecimal feeHtva, BigDecimal feeVat, BigDecimal total) {
    }

    /** Frais à la charge du partenaire et montant à lui rendre. */
    public record FeeAdjustment(FeeBreakdown partnerFee, BigDecimal amountToReturn) {
    }
}