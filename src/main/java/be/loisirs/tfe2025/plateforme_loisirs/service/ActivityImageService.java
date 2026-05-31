package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ActivityImageService {

    private final ActivityRepository activityRepository;
    private final ActivityImageRepository activityImageRepository;
    private final ImageStorageService imageStorageService;

    public ActivityImageService(ActivityRepository activityRepository,
                                ActivityImageRepository activityImageRepository,
                                ImageStorageService imageStorageService) {
        this.activityRepository = activityRepository;
        this.activityImageRepository = activityImageRepository;
        this.imageStorageService = imageStorageService;
    }

    public ActivityImage addImage(Long activityId, MultipartFile file) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        String imageUrl = imageStorageService.storeImage(file, "activities", activityId);

        ActivityImage activityImage = new ActivityImage();
        activityImage.setUrl(imageUrl);
        activityImage.setActivity(activity);

        return activityImageRepository.save(activityImage);
    }

    public List<ActivityImage> getImages(Long activityId) {
        return activityImageRepository.findByActivityId(activityId);
    }

    public void deleteImage(Long activityId, Long imageId) {
        ActivityImage activityImage = activityImageRepository
                .findByIdAndActivityId(imageId, activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable."));

        if ("/uploads/activities/default-activity.png".equals(activityImage.getUrl())) {
            throw new IllegalArgumentException("L'image par défaut ne peut pas être supprimée.");
        }

        imageStorageService.deleteImage(activityImage.getUrl());
        activityImageRepository.delete(activityImage);
    }
}