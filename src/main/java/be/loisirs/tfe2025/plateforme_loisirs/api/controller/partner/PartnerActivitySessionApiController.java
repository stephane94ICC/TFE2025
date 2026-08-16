package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivitySessionRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivitySessionResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivitySessionMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerActivitySessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/partner/activities/{activityId}/sessions")
public class PartnerActivitySessionApiController {

    private final PartnerActivitySessionService partnerActivitySessionService;
    private final ActivitySessionMapper activitySessionMapper;

    public PartnerActivitySessionApiController(
            PartnerActivitySessionService partnerActivitySessionService,
            ActivitySessionMapper activitySessionMapper
    ) {
        this.partnerActivitySessionService = partnerActivitySessionService;
        this.activitySessionMapper = activitySessionMapper;
    }

    @GetMapping
    public ResponseEntity<List<ActivitySessionResponseDTO>> getSessions(
            @PathVariable Long activityId,
            Principal principal
    ) {
        List<ActivitySessionResponseDTO> sessions = partnerActivitySessionService
                .getSessions(activityId, principal.getName())
                .stream()
                .map(activitySessionMapper::toDTO)
                .toList();

        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    public ResponseEntity<ActivitySessionResponseDTO> addSession(
            @PathVariable Long activityId,
            @RequestBody ActivitySessionRequestDTO dto,
            Principal principal
    ) {
        ActivitySession saved = partnerActivitySessionService.addSession(
                activityId, principal.getName(), dto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(activitySessionMapper.toDTO(saved));
    }
}