package be.loisirs.tfe2025.plateforme_loisirs.util;

import java.math.BigDecimal;
import java.math.RoundingMode;


public final class CommissionCalculator {

    public static final BigDecimal PLATFORM_VAT_RATE = new BigDecimal("21.00");
    public static final BigDecimal MIN_COMMISSION_RATE = new BigDecimal("1.00");
    public static final BigDecimal MAX_COMMISSION_RATE = new BigDecimal("8.00");

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private CommissionCalculator() {
    }

    public static CommissionBreakdown calculate(BigDecimal priceTtc,
                                                BigDecimal saleVatRate,
                                                BigDecimal commissionRate) {
        validate(priceTtc, saleVatRate, commissionRate);

        BigDecimal vatFactor = BigDecimal.ONE.add(saleVatRate.movePointLeft(2));
        BigDecimal saleHtva = priceTtc.divide(vatFactor, SCALE, ROUNDING);

        BigDecimal commissionHtva = saleHtva
                .multiply(commissionRate.movePointLeft(2))
                .setScale(SCALE, ROUNDING);

        BigDecimal commissionVat = commissionHtva
                .multiply(PLATFORM_VAT_RATE.movePointLeft(2))
                .setScale(SCALE, ROUNDING);

        BigDecimal commissionTotal = commissionHtva.add(commissionVat);

        return new CommissionBreakdown(
                saleHtva,
                commissionRate.setScale(SCALE, ROUNDING),
                commissionHtva,
                commissionVat,
                commissionTotal
        );
    }

    private static void validate(BigDecimal priceTtc,
                                 BigDecimal saleVatRate,
                                 BigDecimal commissionRate) {
        if (priceTtc == null || saleVatRate == null || commissionRate == null) {
            throw new IllegalArgumentException("Prix, taux de TVA et taux de commission sont obligatoires.");
        }
        if (priceTtc.signum() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif.");
        }
        if (!VatRates.isAllowed(saleVatRate)) {
            throw new IllegalArgumentException("Taux de TVA non autorisé : " + saleVatRate);
        }
        if (commissionRate.compareTo(MIN_COMMISSION_RATE) < 0
                || commissionRate.compareTo(MAX_COMMISSION_RATE) > 0) {
            throw new IllegalArgumentException("Taux de commission hors limites (1 à 8 %) : " + commissionRate);
        }
    }

    /**
     * Résultat du calcul. saleHtva n'est pas stocké : il sert à la lisibilité et aux tests.
     */
    public record CommissionBreakdown(
            BigDecimal saleHtva,
            BigDecimal commissionRate,
            BigDecimal commissionHtva,
            BigDecimal commissionVat,
            BigDecimal commissionTotal
    ) {
    }
}