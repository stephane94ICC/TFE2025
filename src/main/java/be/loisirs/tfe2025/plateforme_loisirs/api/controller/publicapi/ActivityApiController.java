package be.loisirs.tfe2025.plateforme_loisirs.api.controller.publicapi;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ActivityDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityApiController {

    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    public ActivityApiController(ActivityService activityService, ActivityMapper activityMapper) {
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    // lister toutes les activités
    @GetMapping
    public List<ActivityDTO> getAllActivities() {
        return activityService.getAllActivities()
                .stream()
                .map(activityMapper::toDTO)
                .toList();
    }

    // details d'une ACTIVITÉ via ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivity(id);

        if (activity == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(activityMapper.toDTO(activity));
    }

}