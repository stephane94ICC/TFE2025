package be.loisirs.tfe2025.plateforme_loisirs.api.controller.publicapi;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivitySessionResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivitySessionMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.PublicActivitySessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities/{activityId}/sessions")
public class PublicActivitySessionApiController {

    private final PublicActivitySessionService publicActivitySessionService;
    private final ActivitySessionMapper activitySessionMapper;

    public PublicActivitySessionApiController(
            PublicActivitySessionService publicActivitySessionService,
            ActivitySessionMapper activitySessionMapper
    ) {
        this.publicActivitySessionService = publicActivitySessionService;
        this.activitySessionMapper = activitySessionMapper;
    }

    @GetMapping
    public ResponseEntity<List<ActivitySessionResponseDTO>> getSessions(@PathVariable Long activityId) {
        List<ActivitySessionResponseDTO> sessions = publicActivitySessionService
                .getAvailableSessions(activityId)
                .stream()
                .map(activitySessionMapper::toDTO)
                .toList();

        return ResponseEntity.ok(sessions);
    }
}