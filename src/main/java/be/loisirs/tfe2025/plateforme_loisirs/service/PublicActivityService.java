package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityService activityService;

    public PublicActivityService(
            ActivityRepository activityRepository,
            ActivityService activityService
    ) {
        this.activityRepository = activityRepository;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<Activity> getApprovedActivities() {
        return activityRepository
                .findAllByStatusOrderByCreatedAtDesc(ActivityStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public Activity getApprovedActivity(Long id) {
        return activityService.findApprovedById(id);
    }
}