package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.OrderRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StripeWebhookService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final StripeCheckoutService stripeCheckoutService;
    private final ActivityLogService activityLogService;
    private final String stripeWebhookSecret;

    public StripeWebhookService(
            OrderRepository orderRepository,
            ReservationRepository reservationRepository,
            StripeCheckoutService stripeCheckoutService,
            ActivityLogService activityLogService,
            @Value("${stripe.webhook-secret}") String stripeWebhookSecret
    ) {
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
        this.stripeCheckoutService = stripeCheckoutService;
        this.activityLogService = activityLogService;
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

        Optional<Order> optionalOrder = orderRepository.findByStripeSessionId(session.getId());

        if (optionalOrder.isPresent()) {
            confirmOrder(optionalOrder.get(), session);
            return;
        }

        Optional<Reservation> optionalReservation =
                reservationRepository.findByStripeSessionId(session.getId());

        if (optionalReservation.isPresent()) {
            confirmReservation(optionalReservation.get());
            return;
        }

        throw new IllegalArgumentException("Commande ou réservation liée à Stripe introuvable.");
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

        throw new IllegalArgumentException("Commande ou réservation liée à Stripe introuvable.");
    }


    private void confirmOrder(Order order, Session session) {
        if (OrderStatus.PAID.equals(order.getStatus())) {
            return;
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setStripePaymentIntentId(session.getPaymentIntent());
        orderRepository.save(order);

        logConfirmation(
                ActivityEventType.ORDER_PAID,
                order.getUser(),
                "Order",
                order.getId(),
                "Commande n°" + order.getId()
                        + " - " + order.getTotalAmount() + " EUR"
                        + " - paiement confirmé"
        );
    }

    private void confirmReservation(Reservation reservation) {
        if (ReservationStatus.CONFIRMED.equals(reservation.getStatus())) {
            return;
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        logConfirmation(
                ActivityEventType.RESERVATION_CONFIRMED,
                reservation.getUser(),
                "Reservation",
                reservation.getId(),
                reservation.getReference()
                        + " - " + reservation.getSession().getActivity().getTitle()
                        + " - " + reservation.getQuantity() + " place(s)"
                        + " - " + reservation.getTotalPrice() + " EUR"
                        + " - paiement confirmé"
        );
    }

    private void expireOrder(Order order) {
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        stripeCheckoutService.restoreStock(order);
    }

    private void expireReservation(Reservation reservation) {
        if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
            return;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }


    private void logConfirmation(ActivityEventType eventType,
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