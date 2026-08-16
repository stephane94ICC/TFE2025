package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.OrderRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
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
    private final String stripeWebhookSecret;

    public StripeWebhookService(
            OrderRepository orderRepository,
            ReservationRepository reservationRepository,
            StripeCheckoutService stripeCheckoutService,
            @Value("${stripe.webhook-secret}") String stripeWebhookSecret
    ) {
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
        this.stripeCheckoutService = stripeCheckoutService;
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
    }

    private void confirmReservation(Reservation reservation) {
        if (ReservationStatus.CONFIRMED.equals(reservation.getStatus())) {
            return;
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
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

    private Session extractSession(Event event) {
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();

        if (optionalStripeObject.isEmpty() || !(optionalStripeObject.get() instanceof Session session)) {
            throw new IllegalArgumentException("Session Stripe introuvable dans l'événement.");
        }

        return session;
    }
}