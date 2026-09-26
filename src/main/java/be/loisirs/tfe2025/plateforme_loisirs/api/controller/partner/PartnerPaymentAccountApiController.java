package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PartnerPaymentAccountDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerService;
import be.loisirs.tfe2025.plateforme_loisirs.service.StripeConnectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/*
 * Compte de paiement du partenaire connecté.
 * Aucun identifiant dans l'URL : le partenaire est celui du jeton JWT,
 * il ne peut donc agir que sur son propre compte (pas d'IDOR).
 * Accès réservé au rôle PARTNER par SecurityConfig (/api/partner/**).
 */
@RestController
@RequestMapping("/api/partner/payment-account")
public class PartnerPaymentAccountApiController {

    private final PartnerService partnerService;
    private final StripeConnectService stripeConnectService;

    public PartnerPaymentAccountApiController(PartnerService partnerService,
                                              StripeConnectService stripeConnectService) {
        this.partnerService = partnerService;
        this.stripeConnectService = stripeConnectService;
    }

    @GetMapping
    public ResponseEntity<PartnerPaymentAccountDTO> getPaymentAccount(Principal principal) {
        Partner partner = partnerService.getPartnerByUserEmail(principal.getName());

        return ResponseEntity.ok(
                new PartnerPaymentAccountDTO(stripeConnectService.getStatus(partner), null)
        );
    }

    @PostMapping("/onboarding-link")
    public ResponseEntity<PartnerPaymentAccountDTO> createOnboardingLink(Principal principal) {
        String url = stripeConnectService.createOnboardingLink(principal.getName());
        Partner partner = partnerService.getPartnerByUserEmail(principal.getName());

        return ResponseEntity.ok(
                new PartnerPaymentAccountDTO(stripeConnectService.getStatus(partner), url)
        );
    }
}