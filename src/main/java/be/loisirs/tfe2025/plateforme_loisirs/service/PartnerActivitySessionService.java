package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivitySessionRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLocationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivitySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartnerActivitySessionService {

    private final ActivitySessionRepository activitySessionRepository;
    private final ActivityRepository activityRepository;
    private final ActivityLocationRepository activityLocationRepository;

    public PartnerActivitySessionService(
            ActivitySessionRepository activitySessionRepository,
            ActivityRepository activityRepository,
            ActivityLocationRepository activityLocationRepository
    ) {
        this.activitySessionRepository = activitySessionRepository;
        this.activityRepository = activityRepository;
        this.activityLocationRepository = activityLocationRepository;
    }

    public List<ActivitySession> getSessions(Long activityId, String email) {
        return activitySessionRepository
                .findAllByActivity_IdAndActivity_Partner_User_Email(activityId, email);
    }

    @Transactional
    public ActivitySession addSession(Long activityId, String email, ActivitySessionRequestDTO dto) {
        Activity activity = activityRepository.findByIdAndPartner_User_Email(activityId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable."));

        ActivityLocation location = activityLocationRepository
                .findByIdAndPartner_User_Email(dto.getLocationId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu introuvable."));

        if (dto.getStartAt() == null || dto.getEndAt() == null
                || !dto.getEndAt().isAfter(dto.getStartAt())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }

        if (dto.getBookingDeadline() == null || dto.getBookingDeadline().isAfter(dto.getStartAt())) {
            throw new IllegalArgumentException("Le délai de réservation doit précéder le début du créneau.");
        }

        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {
            throw new IllegalArgumentException("La capacité doit être positive.");
        }

        ActivitySession session = new ActivitySession();
        session.setActivity(activity);
        session.setLocation(location);
        session.setStartAt(dto.getStartAt());
        session.setEndAt(dto.getEndAt());
        session.setCapacity(dto.getCapacity());
        session.setBookingDeadline(dto.getBookingDeadline());

        return activitySessionRepository.save(session);
    }
}