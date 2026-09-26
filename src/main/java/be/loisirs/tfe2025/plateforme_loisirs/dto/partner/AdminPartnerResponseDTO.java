package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import be.loisirs.tfe2025.plateforme_loisirs.service.StripeConnectService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPartnerResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String vatNumber;
    private BigDecimal commissionRate;

    // null : état momentanément indisponible chez Stripe (l'écran affiche « — »)
    private StripeConnectService.AccountStatus paymentStatus;
}