package be.loisirs.tfe2025.plateforme_loisirs.util;

import java.math.BigDecimal;
import java.util.List;

public final class VatRates {

    public static final List<BigDecimal> ALLOWED = List.of(
            new BigDecimal("0.00"),
            new BigDecimal("6.00"),
            new BigDecimal("12.00"),
            new BigDecimal("21.00")
    );

    private VatRates() {
    }

    public static boolean isAllowed(BigDecimal rate) {
        return rate != null && ALLOWED.stream().anyMatch(allowed -> allowed.compareTo(rate) == 0);
    }
}