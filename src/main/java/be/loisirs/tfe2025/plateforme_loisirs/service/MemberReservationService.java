package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.RefundNotAvailableException;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.service.StripePaymentFeeService.PaymentFees;
import be.loisirs.tfe2025.plateforme_loisirs.util.PaymentFeeCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemberReservationService {

    private static final Logger log = LoggerFactory.getLogger(MemberReservationService.class);

    private final ReservationRepository reservationRepository;
    private final ActivityLogService activityLogService;
    private final StripeRefundService stripeRefundService;
    private final StripePaymentFeeService stripePaymentFeeService;

    public MemberReservationService(ReservationRepository reservationRepository,
                                    ActivityLogService activityLogService,
                                    StripeRefundService stripeRefundService,
                                    StripePaymentFeeService stripePaymentFeeService) {
        this.reservationRepository = reservationRepository;
        this.activityLogService = activityLogService;
        this.stripeRefundService = stripeRefundService;
        this.stripePaymentFeeService = stripePaymentFeeService;
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
     *
     * Paiement partagé : le partenaire garde à sa charge les frais de paiement, la plateforme
     * lui rend sa commission. Si ce dernier appel échoue, l'annulation échoue aussi. Une
     * nouvelle tentative est sans risque : Stripe rejoue le même remboursement (clé
     * d'idempotence), et le montant à rendre est recalculé sur la retenue actuelle.
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

        // Lu avant de rembourser : si Stripe ne répond pas ici, rien n'a encore bougé.
        Optional<PaymentFees> fees = connectSale
                ? stripePaymentFeeService.findFees(paymentIntentId)
                : Optional.empty();

        // Retenue inconnue : Stripe la rend en entier au partenaire, comme avant ce calcul.
        // La plateforme paie alors les frais, mais le partenaire n'est jamais pénalisé.
        boolean refundWholeRetention = connectSale && fees.isEmpty();

        if (refundWholeRetention) {
            log.warn("Retenue inconnue pour la réservation {} : rendue en entier au partenaire.",
                    reservation.getReference());
        }

        String refundId = stripeRefundService.refund(
                paymentIntentId, reservation.getId(), connectSale, refundWholeRetention);

        BigDecimal commissionReturned = fees
                .map(found -> returnCommission(reservation, found))
                .orElse(BigDecimal.ZERO);

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
                        + (commissionReturned.signum() > 0
                                ? " - commission rendue au partenaire " + commissionReturned + " EUR"
                                : "")
        );
    }

    /*
     * La plateforme garde seulement les frais de paiement à la charge du partenaire, et lui
     * rend le reste de sa retenue : sa commission. Pour une vente antérieure aux frais,
     * il n'y a rien à garder : toute la retenue est rendue, comme avant.
     */
    private BigDecimal returnCommission(Reservation reservation, PaymentFees fees) {
        BigDecimal partnerFee = reservation.getPaymentFeeHtva() == null
                ? BigDecimal.ZERO
                : reservation.getPaymentFeeHtva().add(reservation.getPaymentFeeVat());

        BigDecimal amount = PaymentFeeCalculator.amountToReturnOnRefund(fees.currentRetention(), partnerFee);

        if (amount.signum() > 0) {
            stripePaymentFeeService.returnToPartner(
                    fees.applicationFeeId(),
                    amount,
                    "refund-retention-res-" + reservation.getId()
            );
        }

        return amount;
    }
}