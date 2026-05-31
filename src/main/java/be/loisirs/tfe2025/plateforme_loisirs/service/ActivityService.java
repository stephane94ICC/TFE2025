package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityImageRepository activityImageRepository;

    public ActivityService(ActivityRepository activityRepository,
                           ActivityImageRepository activityImageRepository) {
        this.activityRepository = activityRepository;
        this.activityImageRepository = activityImageRepository;
    }
    public List<Activity> getAllActivities() {

        return activityRepository.findAll();
    }

    public Activity getActivity(Long id) {

        return activityRepository.findById(id).orElse(null);
    }

    @Transactional
    public Activity addActivity(Activity activity) {
        Activity savedActivity = activityRepository.save(activity);

        ActivityImage defaultImage = new ActivityImage();
        defaultImage.setUrl("/uploads/activities/default-activity.png");
        defaultImage.setActivity(savedActivity);
        activityImageRepository.save(defaultImage);

        return savedActivity;
    }

    public Activity updateActivity(Activity activity) {

        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id) {

        activityRepository.deleteById(id);
    }
}