package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PartnerActivityRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityRequestMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartnerActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityService activityService;
    private final ActivityRequestMapper activityRequestMapper;

    public PartnerActivityService(
            ActivityRepository activityRepository,
            ActivityService activityService,
            ActivityRequestMapper activityRequestMapper
    ) {
        this.activityRepository = activityRepository;
        this.activityService = activityService;
        this.activityRequestMapper = activityRequestMapper;
    }

    @Transactional(readOnly = true)
    public List<Activity> getActivities(String partnerEmail) {
        return activityRepository
                .findAllByPartner_User_EmailOrderByCreatedAtDesc(partnerEmail);
    }

    @Transactional(readOnly = true)
    public Activity getActivity(Long id, String partnerEmail) {
        return activityService.findOwnedById(id, partnerEmail);
    }

    @Transactional
    public Activity createActivity(
            String partnerEmail,
            PartnerActivityRequestDTO dto
    ) {
        Partner partner =
                activityService.findPartnerByUserEmail(partnerEmail);

        Activity activity = activityRequestMapper.toEntity(dto);
        activity.setPartner(partner);
        activity.setStatus(ActivityStatus.PENDING_REVIEW);

        return activityService.saveWithDefaultImage(activity);
    }

    @Transactional
    public Activity updateActivity(
            Long id,
            String partnerEmail,
            PartnerActivityRequestDTO dto
    ) {
        Activity activity =
                activityService.findOwnedById(id, partnerEmail);

        activityRequestMapper.updateEntity(dto, activity);

        activity.setStatus(ActivityStatus.PENDING_REVIEW);
        activity.setSubmittedAt(LocalDateTime.now());
        activity.setReviewedAt(null);
        activity.setReviewedByUser(null);
        activity.setReviewComment(null);

        return activityRepository.save(activity);
    }
}