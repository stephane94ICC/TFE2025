package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityReviewRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityRequestMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityService activityService;
    private final ActivityRequestMapper activityRequestMapper;

    public AdminActivityService(
            ActivityRepository activityRepository,
            ActivityService activityService,
            ActivityRequestMapper activityRequestMapper
    ) {
        this.activityRepository = activityRepository;
        this.activityService = activityService;
        this.activityRequestMapper = activityRequestMapper;
    }

    @Transactional(readOnly = true)
    public List<Activity> getActivities() {
        return activityRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Activity getActivity(Long id) {
        return activityService.findAnyById(id);
    }

    @Transactional
    public Activity createActivity(
            AdminActivityRequestDTO dto,
            String adminEmail
    ) {
        Partner partner = activityService.findPartnerById(dto.getPartnerId());
        User admin = activityService.findUserByEmail(adminEmail);

        Activity activity = activityRequestMapper.toEntity(dto);
        activity.setPartner(partner);
        activity.setStatus(ActivityStatus.APPROVED);
        activity.setReviewedAt(LocalDateTime.now());
        activity.setReviewedByUser(admin);

        return activityService.saveWithDefaultImage(activity);
    }

    @Transactional
    public Activity updateActivity(
            Long id,
            AdminActivityRequestDTO dto
    ) {
        Activity activity = activityService.findAnyById(id);
        Partner partner = activityService.findPartnerById(dto.getPartnerId());

        activityRequestMapper.updateEntity(dto, activity);
        activity.setPartner(partner);

        return activityRepository.save(activity);
    }

    @Transactional
    public Activity reviewActivity(
            Long id,
            AdminActivityReviewRequestDTO dto,
            String adminEmail
    ) {
        Activity activity = activityService.findAnyById(id);
        ActivityStatus status = dto.getStatus();

        if (status == null) {
            throw new IllegalArgumentException("Le statut est obligatoire.");
        }

        activity.setStatus(status);
        activity.setReviewComment(dto.getReviewComment());

        if (status == ActivityStatus.PENDING_REVIEW) {
            activity.setSubmittedAt(LocalDateTime.now());
            activity.setReviewedAt(null);
            activity.setReviewedByUser(null);
        } else {
            activity.setReviewedAt(LocalDateTime.now());
            activity.setReviewedByUser(
                    activityService.findUserByEmail(adminEmail)
            );
        }

        return activityRepository.save(activity);
    }
}