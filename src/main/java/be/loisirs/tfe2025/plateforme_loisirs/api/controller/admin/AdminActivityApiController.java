package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityReviewRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityResponseMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.AdminActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/activities")
public class AdminActivityApiController {

    private final AdminActivityService adminActivityService;
    private final ActivityResponseMapper activityResponseMapper;

    public AdminActivityApiController(
            AdminActivityService adminActivityService,
            ActivityResponseMapper activityResponseMapper
    ) {
        this.adminActivityService = adminActivityService;
        this.activityResponseMapper = activityResponseMapper;
    }

    @GetMapping
    public List<AdminActivityResponseDTO> getActivities() {
        return adminActivityService.getActivities()
                .stream()
                .map(activityResponseMapper::toAdminDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminActivityResponseDTO> getActivity(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                activityResponseMapper.toAdminDTO(
                        adminActivityService.getActivity(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<AdminActivityResponseDTO> createActivity(
            @Valid @RequestBody AdminActivityRequestDTO dto,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                activityResponseMapper.toAdminDTO(
                        adminActivityService.createActivity(
                                dto,
                                principal.getName()
                        )
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody AdminActivityRequestDTO dto
    ) {
        return ResponseEntity.ok(
                activityResponseMapper.toAdminDTO(
                        adminActivityService.updateActivity(id, dto)
                )
        );
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<AdminActivityResponseDTO> reviewActivity(
            @PathVariable Long id,
            @Valid @RequestBody AdminActivityReviewRequestDTO dto,
            Principal principal
    ) {
        return ResponseEntity.ok(
                activityResponseMapper.toAdminDTO(
                        adminActivityService.reviewActivity(
                                id,
                                dto,
                                principal.getName()
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableActivity(
            @PathVariable Long id,
            Principal principal
    ) {
        AdminActivityReviewRequestDTO dto =
                new AdminActivityReviewRequestDTO();

        dto.setStatus(ActivityStatus.DISABLED);
        dto.setReviewComment("Activité désactivée par l'administrateur.");

        adminActivityService.reviewActivity(
                id,
                dto,
                principal.getName()
        );

        return ResponseEntity.noContent().build();
    }
}