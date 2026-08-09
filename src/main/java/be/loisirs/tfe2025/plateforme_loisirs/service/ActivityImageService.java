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

    // l'Admin : à accès à n'importe quelle activité

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

    // Le Partenaire : est limité à ses propres activités

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

        ActivityImage activityImage = new ActivityImage();
        activityImage.setUrl(imageUrl);
        activityImage.setActivity(activity);

        return activityImageRepository.save(activityImage);
    }

    private void removeImageFromActivity(Activity activity, Long imageId) {
        ActivityImage activityImage = activity.getImages()
                .stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable."));

        if ("/uploads/activities/default-activity.png".equals(activityImage.getUrl())) {
            throw new IllegalArgumentException("L'image par défaut ne peut pas être supprimée.");
        }

        imageStorageService.deleteImage(activityImage.getUrl());
        activity.getImages().remove(activityImage);
        activityRepository.save(activity);
    }
}