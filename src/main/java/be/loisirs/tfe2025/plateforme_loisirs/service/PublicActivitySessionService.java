package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivitySessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicActivitySessionService {

    private final ActivitySessionRepository activitySessionRepository;

    public PublicActivitySessionService(ActivitySessionRepository activitySessionRepository) {
        this.activitySessionRepository = activitySessionRepository;
    }

    public List<ActivitySession> getAvailableSessions(Long activityId) {
        return activitySessionRepository
                .findAllByActivity_IdAndStatus(activityId, ActivitySessionStatus.SCHEDULED);
    }
}