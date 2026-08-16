package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ReservationResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponseDTO toDTO(Reservation reservation) {
        if (reservation == null) return null;

        ActivitySession session = reservation.getSession();

        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setReference(reservation.getReference());
        dto.setSessionId(session.getId());
        dto.setActivityTitle(session.getActivity().getTitle());
        dto.setSessionStartAt(session.getStartAt());
        dto.setQuantity(reservation.getQuantity());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus());
        dto.setBookedAt(reservation.getBookedAt());

        return dto;
    }
}