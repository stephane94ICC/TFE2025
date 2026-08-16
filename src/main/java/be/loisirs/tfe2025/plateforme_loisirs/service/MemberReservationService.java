package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberReservationService {

    private final ReservationRepository reservationRepository;

    public MemberReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getReservations(String email) {
        return reservationRepository.findAllByUser_Email(email);
    }
}