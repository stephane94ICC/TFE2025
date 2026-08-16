package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivitySessionResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivitySessionMapper {

    private final ReservationRepository reservationRepository;

    public ActivitySessionMapper(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ActivitySessionResponseDTO toDTO(ActivitySession session) {
        if (session == null) return null;

        int booked = reservationRepository.sumQuantityBySessionIdAndStatusIn(
                session.getId(),
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        ActivitySessionResponseDTO dto = new ActivitySessionResponseDTO();
        dto.setId(session.getId());
        dto.setActivityId(session.getActivity().getId());
        dto.setLocationId(session.getLocation().getId());
        dto.setLocationName(session.getLocation().getName());
        dto.setStartAt(session.getStartAt());
        dto.setEndAt(session.getEndAt());
        dto.setCapacity(session.getCapacity());
        dto.setRemainingSeats(session.getCapacity() - booked);
        dto.setStatus(session.getStatus());
        dto.setBookingDeadline(session.getBookingDeadline());

        return dto;
    }
}