package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PartnerUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.PartnerMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/partner/profile")
public class PartnerProfileApiController {

    private final PartnerService partnerService;
    private final PartnerMapper partnerMapper;

    public PartnerProfileApiController(
            PartnerService partnerService,
            PartnerMapper partnerMapper
    ) {
        this.partnerService = partnerService;
        this.partnerMapper = partnerMapper;
    }

    @GetMapping
    public ResponseEntity<PartnerResponseDTO> getProfile(Principal principal) {
        Partner partner = partnerService.getPartnerByUserEmail(principal.getName());

        return ResponseEntity.ok(partnerMapper.toDTO(partner));
    }

    @PutMapping
    public ResponseEntity<PartnerResponseDTO> updateProfile(
            @RequestBody PartnerUpdateDTO dto,
            Principal principal
    ) {
        Partner partner = partnerService.getPartnerByUserEmail(principal.getName());

        partnerMapper.updateEntity(dto, partner);

        Partner updatedPartner = partnerService.updatePartner(
                partner.getId(),
                partner
        );

        return ResponseEntity.ok(partnerMapper.toDTO(updatedPartner));
    }

    @PutMapping("/logo")
    public ResponseEntity<PartnerResponseDTO> updateLogo(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        Partner updatedPartner = partnerService.updateLogo(
                principal.getName(),
                file
        );

        return ResponseEntity.ok(partnerMapper.toDTO(updatedPartner));
    }
}