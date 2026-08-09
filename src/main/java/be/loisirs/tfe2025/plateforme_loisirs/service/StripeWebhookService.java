package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.OrderRepository;
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
    private final String stripeWebhookSecret;

    public StripeWebhookService(
            OrderRepository orderRepository,
            @Value("${stripe.webhook-secret}") String stripeWebhookSecret
    ) {
        this.orderRepository = orderRepository;
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
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();

        if (optionalStripeObject.isEmpty() || !(optionalStripeObject.get() instanceof Session session)) {
            throw new IllegalArgumentException("Session Stripe introuvable dans l'événement.");
        }

        Order order = orderRepository.findByStripeSessionId(session.getId())
                .orElseThrow(() -> new IllegalArgumentException("Commande liée à Stripe introuvable."));

        if (OrderStatus.PAID.equals(order.getStatus())) {
            return;
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setStripePaymentIntentId(session.getPaymentIntent());
        orderRepository.save(order);
    }
}
