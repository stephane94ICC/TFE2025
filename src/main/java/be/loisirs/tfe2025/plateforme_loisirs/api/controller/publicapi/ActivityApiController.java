package be.loisirs.tfe2025.plateforme_loisirs.api.controller.publicapi;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PublicActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.PublicActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityApiController {

    private final PublicActivityService publicActivityService;

    public ActivityApiController(
            PublicActivityService publicActivityService
    ) {
        this.publicActivityService = publicActivityService;
    }

    @GetMapping
    public List<PublicActivityResponseDTO> getApprovedActivities() {
        return publicActivityService.getApprovedActivities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicActivityResponseDTO> getApprovedActivity(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                publicActivityService.getApprovedActivity(id)
        );
    }
}