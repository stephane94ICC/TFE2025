package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.dto.ActivityDTO;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/activities")
public class AdminActivityApiController {
    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    public AdminActivityApiController(ActivityService activityService, ActivityMapper activityMapper) {
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    //liste des activités version admin
    @GetMapping
    public List<ActivityDTO> getAllActivities() {
        return activityService.getAllActivities()
                .stream()
                .map(activityMapper::toDTO)
                .toList();
    }

      // Détail d’une activité version admin
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivity(id);

        if (activity == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(activityMapper.toDTO(activity));
    }

    // Création d’une activité
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO dto) {

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (dto.getPartnerId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Activity activity = activityMapper.toEntity(dto);
        Activity savedActivity = activityService.addActivity(activity);

        return ResponseEntity.ok(activityMapper.toDTO(savedActivity));
    }

    // Modification d’une activité
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO dto) {

        Activity existing = activityService.getActivity(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        activityMapper.updateEntity(dto, existing);
        Activity updatedActivity = activityService.updateActivity(existing);

        return ResponseEntity.ok(activityMapper.toDTO(updatedActivity));
    }

    // Suppression d’une activité
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {

        Activity existing = activityService.getActivity(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        activityService.deleteActivity(id);

        return ResponseEntity.noContent().build();
    }
}