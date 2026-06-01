package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PartnerActivityRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PartnerActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityResponseMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/partner/activities")
public class PartnerActivityApiController {

    private final PartnerActivityService partnerActivityService;
    private final ActivityResponseMapper activityResponseMapper;

    public PartnerActivityApiController(
            PartnerActivityService partnerActivityService,
            ActivityResponseMapper activityResponseMapper
    ) {
        this.partnerActivityService = partnerActivityService;
        this.activityResponseMapper = activityResponseMapper;
    }

    @GetMapping
    public List<PartnerActivityResponseDTO> getActivities(Principal principal) {
        return partnerActivityService.getActivities(principal.getName())
                .stream()
                .map(activityResponseMapper::toPartnerDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerActivityResponseDTO> getActivity(
            @PathVariable Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(
                activityResponseMapper.toPartnerDTO(
                        partnerActivityService.getActivity(id, principal.getName())
                )
        );
    }

    @PostMapping
    public ResponseEntity<PartnerActivityResponseDTO> createActivity(
            @Valid @RequestBody PartnerActivityRequestDTO dto,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                activityResponseMapper.toPartnerDTO(
                        partnerActivityService.createActivity(
                                principal.getName(),
                                dto
                        )
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody PartnerActivityRequestDTO dto,
            Principal principal
    ) {
        return ResponseEntity.ok(
                activityResponseMapper.toPartnerDTO(
                        partnerActivityService.updateActivity(
                                id,
                                principal.getName(),
                                dto
                        )
                )
        );
    }
}