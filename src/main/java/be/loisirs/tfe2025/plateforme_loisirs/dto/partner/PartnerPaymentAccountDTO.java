package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import be.loisirs.tfe2025.plateforme_loisirs.service.StripeConnectService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerPaymentAccountDTO {

    private StripeConnectService.AccountStatus status;

    // Rempli uniquement quand le partenaire demande le lien d'inscription
    private String onboardingUrl;
}