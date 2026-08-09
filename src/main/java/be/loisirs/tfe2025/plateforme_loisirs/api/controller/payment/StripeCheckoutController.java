package be.loisirs.tfe2025.plateforme_loisirs.api.controller.payment;

import be.loisirs.tfe2025.plateforme_loisirs.dto.payment.CheckoutRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.payment.CheckoutResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.StripeCheckoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/member/checkout")
public class StripeCheckoutController {

    private final StripeCheckoutService stripeCheckoutService;

    public StripeCheckoutController(StripeCheckoutService stripeCheckoutService) {
        this.stripeCheckoutService = stripeCheckoutService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<CheckoutResponseDTO> createCheckoutSession(
            @RequestBody CheckoutRequestDTO request,
            Principal principal
    ) {
        CheckoutResponseDTO response = stripeCheckoutService.createCheckoutSession(
                principal.getName(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Void> cancelCheckoutSession(
            @RequestParam("session_id") String sessionId,
            Principal principal
    ) {
        stripeCheckoutService.cancelCheckoutSession(
                principal.getName(),
                sessionId
        );

        return ResponseEntity.noContent().build();
    }
}