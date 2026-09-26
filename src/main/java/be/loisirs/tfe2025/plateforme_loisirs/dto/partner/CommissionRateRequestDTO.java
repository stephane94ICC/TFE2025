package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommissionRateRequestDTO {

    // Même fourchette que la contrainte chk_partner_commission_rate (V14_1)
    @NotNull(message = "Le taux de commission est obligatoire.")
    @DecimalMin(value = "1.00", message = "Le taux de commission doit être d'au moins 1 %.")
    @DecimalMax(value = "8.00", message = "Le taux de commission ne peut pas dépasser 8 %.")
    @Digits(integer = 1, fraction = 2, message = "Le taux de commission accepte au plus deux décimales.")
    private BigDecimal commissionRate;
}