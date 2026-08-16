package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivityLocationRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivityLocationResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityLocationMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerActivityLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/partner/locations")
public class PartnerActivityLocationApiController {

    private final PartnerActivityLocationService partnerActivityLocationService;
    private final ActivityLocationMapper activityLocationMapper;

    public PartnerActivityLocationApiController(
            PartnerActivityLocationService partnerActivityLocationService,
            ActivityLocationMapper activityLocationMapper
    ) {
        this.partnerActivityLocationService = partnerActivityLocationService;
        this.activityLocationMapper = activityLocationMapper;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLocationResponseDTO>> getLocations(Principal principal) {
        List<ActivityLocationResponseDTO> locations = partnerActivityLocationService
                .getLocations(principal.getName())
                .stream()
                .map(activityLocationMapper::toDTO)
                .toList();

        return ResponseEntity.ok(locations);
    }

    @PostMapping
    public ResponseEntity<ActivityLocationResponseDTO> addLocation(
            @RequestBody ActivityLocationRequestDTO dto,
            Principal principal
    ) {
        ActivityLocation saved = partnerActivityLocationService.addLocation(principal.getName(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(activityLocationMapper.toDTO(saved));
    }
}