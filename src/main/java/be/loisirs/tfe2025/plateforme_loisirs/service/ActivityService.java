package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    private static final String DEFAULT_ACTIVITY_IMAGE =
            "/uploads/activities/default-activity.png";

    private final ActivityRepository activityRepository;
    private final ActivityImageRepository activityImageRepository;
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            ActivityImageRepository activityImageRepository,
            PartnerRepository partnerRepository,
            UserRepository userRepository
    ) {
        this.activityRepository = activityRepository;
        this.activityImageRepository = activityImageRepository;
        this.partnerRepository = partnerRepository;
        this.userRepository = userRepository;
    }

    public Activity findAnyById(Long id) {
        return activityRepository.findOneById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activité introuvable.")
                );
    }

    public Activity findApprovedById(Long id) {
        return activityRepository.findByIdAndStatus(id, ActivityStatus.APPROVED)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activité introuvable.")
                );
    }

    public Activity findOwnedById(Long id, String partnerEmail) {
        return activityRepository
                .findByIdAndPartner_User_Email(id, partnerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activité introuvable.")
                );
    }

    public Partner findPartnerById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Partenaire introuvable.")
                );
    }

    public Partner findPartnerByUserEmail(String email) {
        return partnerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Partenaire introuvable.")
                );
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Utilisateur introuvable.")
                );
    }

    public Activity saveWithDefaultImage(Activity activity) {
        Activity savedActivity = activityRepository.save(activity);

        ActivityImage defaultImage = new ActivityImage();
        defaultImage.setUrl(DEFAULT_ACTIVITY_IMAGE);
        defaultImage.setActivity(savedActivity);

        activityImageRepository.save(defaultImage);
        savedActivity.getImages().add(defaultImage);

        return savedActivity;
    }
}