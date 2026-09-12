package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ActivityImageService {

    private static final String DEFAULT_ACTIVITY_IMAGE =
            "/uploads/activities/default-activity.png";

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

    // l'Admin : accès à n'importe quelle activité

    @Transactional
    public ActivityImage addImage(Long activityId, MultipartFile file) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        return saveImage(activity, file);
    }

    public List<ActivityImage> getImages(Long activityId) {
        return activityImageRepository.findByActivityId(activityId);
    }

    @Transactional
    public void deleteImage(Long activityId, Long imageId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        removeImageFromActivity(activity, imageId);
    }

    // Le Partenaire : limité à ses propres activités

    @Transactional
    public ActivityImage addImage(Long activityId, String partnerEmail, MultipartFile file) {
        Activity activity = activityRepository
                .findByIdAndPartner_User_Email(activityId, partnerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        return saveImage(activity, file);
    }

    public List<ActivityImage> getImages(Long activityId, String partnerEmail) {
        activityRepository.findByIdAndPartner_User_Email(activityId, partnerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        return activityImageRepository.findByActivityId(activityId);
    }

    @Transactional
    public void deleteImage(Long activityId, Long imageId, String partnerEmail) {
        Activity activity = activityRepository
                .findByIdAndPartner_User_Email(activityId, partnerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        removeImageFromActivity(activity, imageId);
    }

    // logique commune

    private ActivityImage saveImage(Activity activity, MultipartFile file) {
        String imageUrl = imageStorageService.storeImage(file, "activities", activity.getId());

        removeDefaultImage(activity);

        ActivityImage activityImage = new ActivityImage();
        activityImage.setUrl(imageUrl);
        activityImage.setActivity(activity);

        activity.getImages().add(activityImage);
        return activityImageRepository.save(activityImage);
    }

    private void removeImageFromActivity(Activity activity, Long imageId) {
        ActivityImage activityImage = activity.getImages()
                .stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable."));

        if (DEFAULT_ACTIVITY_IMAGE.equals(activityImage.getUrl())) {
            throw new IllegalArgumentException("L'image par défaut ne peut pas être supprimée.");
        }

        imageStorageService.deleteImage(activityImage.getUrl());
        activity.getImages().remove(activityImage);

        if (activity.getImages().isEmpty()) {
            ActivityImage defaultImage = new ActivityImage();
            defaultImage.setUrl(DEFAULT_ACTIVITY_IMAGE);
            defaultImage.setActivity(activity);
            activity.getImages().add(defaultImage);
            activityImageRepository.save(defaultImage);
        }
    }

    private void removeDefaultImage(Activity activity) {
        activity.getImages().removeIf(
                image -> DEFAULT_ACTIVITY_IMAGE.equals(image.getUrl())
        );
    }
}
