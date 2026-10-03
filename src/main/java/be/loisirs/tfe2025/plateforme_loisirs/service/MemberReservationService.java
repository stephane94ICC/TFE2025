package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.RefundNotAvailableException;
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
    private final StripeRefundService stripeRefundService;

    public MemberReservationService(ReservationRepository reservationRepository,
                                    ActivityLogService activityLogService,
                                    StripeRefundService stripeRefundService) {
        this.reservationRepository = reservationRepository;
        this.activityLogService = activityLogService;
        this.stripeRefundService = stripeRefundService;
    }

    public List<Reservation> getReservations(String email) {
        return reservationRepository.findAllByUser_EmailOrderByBookedAtDesc(email);
    }

    /*
     * Annulation par le membre = remboursement intégral.
     * Règle : on n'annule jamais une vente payée sans la rembourser.
     *
     * Ordre : contrôles, puis Stripe, puis la base.
     * Si Stripe échoue, rien ne change en base : la réservation reste valide.
     */
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

        // Vente ancienne sans paiement Stripe enregistré : remboursement automatique impossible.
        String paymentIntentId = reservation.getStripePaymentIntentId();

        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            throw new RefundNotAvailableException(
                    "Remboursement automatique impossible pour cette réservation."
            );
        }

        // Paramètres décidés d'après la vente figée, pas d'après l'état actuel du partenaire :
        // une commission enregistrée signifie un paiement partagé avec le partenaire.
        boolean connectSale = reservation.getCommissionRate() != null;

        String refundId = stripeRefundService.refund(paymentIntentId, reservation.getId(), connectSale);

        LocalDateTime now = LocalDateTime.now();

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(now);
        reservation.setStripeRefundId(refundId);
        reservation.setRefundedAt(now);
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
                        + " - remboursée (" + refundId + ")"
        );
    }
}