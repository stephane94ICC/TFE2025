package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberReservationService {

    private final ReservationRepository reservationRepository;
    private final ActivityLogService activityLogService;

    public MemberReservationService(ReservationRepository reservationRepository,
                                    ActivityLogService activityLogService) {
        this.reservationRepository = reservationRepository;
        this.activityLogService = activityLogService;
    }

    public List<Reservation> getReservations(String email) {
        return reservationRepository.findAllByUser_Email(email);
    }


    @Transactional
    public void cancelReservation(String email, Long reservationId) {

        Reservation reservation = reservationRepository
                .findByIdAndUser_Email(reservationId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        if (!ReservationStatus.CONFIRMED.equals(reservation.getStatus())) {
            throw new IllegalArgumentException(
                    "Seule une réservation confirmée peut être annulée."
            );
        }

        if (LocalDateTime.now().isAfter(reservation.getSession().getBookingDeadline())) {
            throw new IllegalArgumentException(
                    "Le délai d'annulation pour ce créneau est dépassé."
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        activityLogService.log(
                ActivityEventType.RESERVATION_CANCELLED,
                "Reservation",
                reservation.getId(),
                reservation.getReference()
                        + " - " + reservation.getSession().getActivity().getTitle()
                        + " - " + reservation.getQuantity() + " place(s)"
                        + " - " + reservation.getTotalPrice() + " EUR"
                        + " - annulée par le membre"
        );
    }
}