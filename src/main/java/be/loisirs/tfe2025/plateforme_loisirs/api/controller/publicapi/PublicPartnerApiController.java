package be.loisirs.tfe2025.plateforme_loisirs.api.controller.publicapi;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PublicPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.PublicPartnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/partners")
public class PublicPartnerApiController {

    private final PublicPartnerService publicPartnerService;

    public PublicPartnerApiController(
            PublicPartnerService publicPartnerService
    ) {
        this.publicPartnerService = publicPartnerService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PublicPartnerResponseDTO> getPartner(
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(
                publicPartnerService.getActivePartnerBySlug(slug)
        );
    }
}