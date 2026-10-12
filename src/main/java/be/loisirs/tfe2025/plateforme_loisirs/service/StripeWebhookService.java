package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.OrderRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.service.StripePaymentFeeService.PaymentFees;
import be.loisirs.tfe2025.plateforme_loisirs.util.PaymentFeeCalculator;
import be.loisirs.tfe2025.plateforme_loisirs.util.PaymentFeeCalculator.FeeAdjustment;
import be.loisirs.tfe2025.plateforme_loisirs.util.StripeBillingAddress;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class StripeWebhookService {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookService.class);

    private static final String PAYMENT_STATUS_PAID = "paid";

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final StripeCheckoutService stripeCheckoutService;
    private final ActivityLogService activityLogService;
    private final StripePaymentFeeService stripePaymentFeeService;
    private final String stripeWebhookSecret;

    public StripeWebhookService(
            OrderRepository orderRepository,
            ReservationRepository reservationRepository,
            StripeCheckoutService stripeCheckoutService,
            ActivityLogService activityLogService,
            StripePaymentFeeService stripePaymentFeeService,
            @Value("${stripe.webhook-secret}") String stripeWebhookSecret
    ) {
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
        this.stripeCheckoutService = stripeCheckoutService;
        this.activityLogService = activityLogService;
        this.stripePaymentFeeService = stripePaymentFeeService;
        this.stripeWebhookSecret = stripeWebhookSecret;
    }

    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new IllegalStateException("Le secret webhook Stripe n'est pas configuré.");
        }

        Event event = constructEvent(payload, signatureHeader);

        if ("checkout.session.completed".equals(event.getType())) {
            handleCheckoutSessionCompleted(event);
        } else if ("checkout.session.expired".equals(event.getType())) {
            handleCheckoutSessionExpired(event);
        }
    }

    private Event constructEvent(String payload, String signatureHeader) {
        try {
            return Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException exception) {
            throw new IllegalArgumentException("Signature Stripe invalide.");
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session = extractSession(event);

        /*
         * « completed » signifie que le client a terminé le parcours Stripe,
         * pas forcément que l'argent est encaissé (moyens de paiement différés).
         * Seul le statut « paid » autorise une confirmation.
         */
        if (!PAYMENT_STATUS_PAID.equals(session.getPaymentStatus())) {
            log.info("Session Stripe {} terminée mais non payée (statut : {}) : aucune confirmation.",
                    session.getId(), session.getPaymentStatus());
            return;
        }

        Optional<Order> optionalOrder = orderRepository.findByStripeSessionId(session.getId());
        if (optionalOrder.isPresent()) {
            confirmOrder(optionalOrder.get(), session);
            return;
        }

        Optional<Reservation> optionalReservation =
                reservationRepository.findByStripeSessionId(session.getId());
        if (optionalReservation.isPresent()) {
            confirmReservation(optionalReservation.get(), session);
            return;
        }

        logUnknownSession(event, session);
    }

    private void handleCheckoutSessionExpired(Event event) {
        Session session = extractSession(event);

        Optional<Order> optionalOrder = orderRepository.findByStripeSessionId(session.getId());
        if (optionalOrder.isPresent()) {
            expireOrder(optionalOrder.get());
            return;
        }

        Optional<Reservation> optionalReservation =
                reservationRepository.findByStripeSessionId(session.getId());
        if (optionalReservation.isPresent()) {
            expireReservation(optionalReservation.get());
            return;
        }

        logUnknownSession(event, session);
    }

    /*
     * Session inconnue : on répond quand même 200.
     * Répondre une erreur demanderait à Stripe de réessayer, or un nouvel
     * envoi ne fera jamais apparaître une vente absente de la base
     * (session créée par la CLI Stripe, base réinitialisée, autre environnement).
     */
    private void logUnknownSession(Event event, Session session) {
        log.warn("Webhook Stripe {} ignoré : session {} inconnue en base.",
                event.getType(), session.getId());
    }

    /*
     * Transitions autorisées au paiement :
     *   PENDING   -> PAID       (cas normal)
     *   PAID      -> rien       (événement renvoyé par Stripe : idempotence)
     *   CANCELLED -> rien, mais anomalie journalisée : l'argent est encaissé
     *                sur une vente annulée, il faut rembourser.
     */
    private void confirmOrder(Order order, Session session) {
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            reportPaymentOnCancelledOrder(order, session);
            return;
        }

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            return;
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setStripePaymentIntentId(session.getPaymentIntent());
        orderRepository.save(order);

        logSystemEvent(
                ActivityEventType.ORDER_PAID,
                order.getUser(),
                "Order",
                order.getId(),
                "Commande n°" + order.getId()
                        + " - " + order.getTotalAmount() + " EUR"
                        + " - paiement confirmé"
        );
    }

    /*
     * Mêmes transitions que pour une commande :
     *   PENDING   -> CONFIRMED
     *   CONFIRMED -> rien (idempotence)
     *   CANCELLED -> rien, anomalie journalisée
     */
    private void confirmReservation(Reservation reservation, Session session) {
        if (ReservationStatus.CANCELLED.equals(reservation.getStatus())) {
            reportPaymentOnCancelledReservation(reservation, session);
            return;
        }

        if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
            return;
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        // Indispensable pour pouvoir rembourser : Stripe rembourse un payment intent.
        reservation.setStripePaymentIntentId(session.getPaymentIntent());
        // Adresse saisie dans Stripe Checkout, figée comme le nom et l'e-mail de facturation.
        StripeBillingAddress.copyTo(reservation, session.getCustomerDetails());
        adjustPaymentFee(reservation, session.getPaymentIntent());
        reservationRepository.save(reservation);

        logSystemEvent(
                ActivityEventType.RESERVATION_CONFIRMED,
                reservation.getUser(),
                "Reservation",
                reservation.getId(),
                reservation.getReference()
                        + " - " + reservation.getSession().getActivity().getTitle()
                        + " - " + reservation.getQuantity() + " place(s)"
                        + " - " + reservation.getTotalPrice() + " EUR"
                        + paymentFeeDetails(reservation)
                        + " - paiement confirmé"
        );
    }

    /*
     * Au paiement, la retenue de la plateforme contenait les frais maximaux : la carte du
     * client était inconnue. On lit maintenant les frais réels chez Stripe et on rend la
     * différence au partenaire.
     *
     * Si Stripe ne répond pas, l'exception annule toute la confirmation, et Stripe renverra
     * l'événement plus tard. La différence est calculée sur ce que la plateforme retient
     * encore : un renvoi ne rend donc jamais deux fois.
     */
    private void adjustPaymentFee(Reservation reservation, String paymentIntentId) {
        // Vente créée avant le calcul des frais : sa retenue ne contient que la commission.
        if (reservation.getPaymentFeeHtva() == null || paymentIntentId == null) {
            return;
        }

        Optional<PaymentFees> fees = stripePaymentFeeService.findFees(paymentIntentId);

        if (fees.isEmpty()) {
            log.warn("Frais réels indisponibles pour la réservation {} : frais maximaux conservés.",
                    reservation.getReference());
            return;
        }

        FeeAdjustment adjustment = PaymentFeeCalculator.adjust(
                fees.get().currentRetention(),
                reservation.getCommissionHtva().add(reservation.getCommissionVat()),
                reservation.getPaymentFeeHtva(),
                fees.get().stripeFee()
        );

        if (adjustment.amountToReturn().signum() > 0) {
            stripePaymentFeeService.returnToPartner(
                    fees.get().applicationFeeId(),
                    adjustment.amountToReturn(),
                    "fee-adjustment-res-" + reservation.getId()
            );
        }

        reservation.setPaymentFeeHtva(adjustment.partnerFee().feeHtva());
        reservation.setPaymentFeeVat(adjustment.partnerFee().feeVat());
    }

    private String paymentFeeDetails(Reservation reservation) {
        if (reservation.getPaymentFeeHtva() == null) {
            return "";
        }

        BigDecimal total = reservation.getPaymentFeeHtva().add(reservation.getPaymentFeeVat());

        return " - frais de paiement " + total + " EUR TVAC";
    }

    /*
     * Le statut reste CANCELLED : les places ou le stock ont déjà été rendus,
     * confirmer créerait une surréservation ou un stock faux.
     * Le payment intent est enregistré pour permettre le remboursement.
     * S'il l'est déjà avec la même valeur, l'anomalie a déjà été signalée :
     * un renvoi de Stripe ne crée pas de doublon dans le journal.
     */
    private void reportPaymentOnCancelledOrder(Order order, Session session) {
        String paymentIntentId = session.getPaymentIntent();

        if (paymentIntentId != null && Objects.equals(paymentIntentId, order.getStripePaymentIntentId())) {
            return;
        }

        order.setStripePaymentIntentId(paymentIntentId);
        orderRepository.save(order);

        log.warn("Paiement reçu sur la commande annulée n°{} (payment intent {}) : à rembourser.",
                order.getId(), paymentIntentId);

        logSystemEvent(
                ActivityEventType.PAYMENT_ON_CANCELLED_SALE,
                order.getUser(),
                "Order",
                order.getId(),
                "Commande n°" + order.getId()
                        + " - " + order.getTotalAmount() + " EUR"
                        + " - payment intent " + paymentIntentId
                        + " - paiement reçu sur une commande annulée, à rembourser"
        );
    }

    private void reportPaymentOnCancelledReservation(Reservation reservation, Session session) {
        String paymentIntentId = session.getPaymentIntent();

        if (paymentIntentId != null && Objects.equals(paymentIntentId, reservation.getStripePaymentIntentId())) {
            return;
        }

        reservation.setStripePaymentIntentId(paymentIntentId);
        reservationRepository.save(reservation);

        log.warn("Paiement reçu sur la réservation annulée {} (payment intent {}) : à rembourser.",
                reservation.getReference(), paymentIntentId);

        logSystemEvent(
                ActivityEventType.PAYMENT_ON_CANCELLED_SALE,
                reservation.getUser(),
                "Reservation",
                reservation.getId(),
                reservation.getReference()
                        + " - " + reservation.getTotalPrice() + " EUR"
                        + " - payment intent " + paymentIntentId
                        + " - paiement reçu sur une réservation annulée, à rembourser"
        );
    }

    private void expireOrder(Order order) {
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        stripeCheckoutService.restoreStock(order);

        // Journalisé en dernier : logSystem écrit dans sa propre transaction (REQUIRES_NEW)
        logSystemEvent(
                ActivityEventType.ORDER_CANCELLED,
                order.getUser(),
                "Order",
                order.getId(),
                "Commande n°" + order.getId()
                        + " - " + order.getTotalAmount() + " EUR"
                        + " - session de paiement expirée"
        );
    }

    private void expireReservation(Reservation reservation) {
        if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
            return;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        logSystemEvent(
                ActivityEventType.RESERVATION_CANCELLED,
                reservation.getUser(),
                "Reservation",
                reservation.getId(),
                reservation.getReference()
                        + " - " + reservation.getSession().getActivity().getTitle()
                        + " - " + reservation.getQuantity() + " place(s)"
                        + " - session de paiement expirée"
        );
    }

    private void logSystemEvent(ActivityEventType eventType,
                                User user,
                                String targetType,
                                Long targetId,
                                String details) {
        Long userId = (user == null) ? null : user.getId();
        String email = (user == null) ? null : user.getEmail();

        activityLogService.logSystem(eventType, userId, email, targetType, targetId, details);
    }

    private Session extractSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject().orElse(null);

        if (stripeObject == null) {
            try {
                stripeObject = deserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException exception) {
                throw new IllegalArgumentException("Session Stripe illisible dans l'événement.");
            }
        }

        if (!(stripeObject instanceof Session session)) {
            throw new IllegalArgumentException("Session Stripe introuvable dans l'événement.");
        }

        return session;
    }
}