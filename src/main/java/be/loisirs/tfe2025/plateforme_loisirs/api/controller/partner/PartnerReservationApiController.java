package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.PartnerReservationResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * Ventes du partenaire connecté. Réservé au rôle PARTNER par SecurityConfig
 * (règle sur /api/partner/**).
 */
@RestController
@RequestMapping("/api/partner/reservations")
public class PartnerReservationApiController {

    private final PartnerReservationService partnerReservationService;

    public PartnerReservationApiController(PartnerReservationService partnerReservationService) {
        this.partnerReservationService = partnerReservationService;
    }

    @GetMapping
    public ResponseEntity<List<PartnerReservationResponseDTO>> getSales(Principal principal) {
        return ResponseEntity.ok(partnerReservationService.getSales(principal.getName()));
    }
}