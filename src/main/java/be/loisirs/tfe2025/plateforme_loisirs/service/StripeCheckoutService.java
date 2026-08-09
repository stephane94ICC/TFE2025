package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.payment.CheckoutItemRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.payment.CheckoutRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.payment.CheckoutResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Order;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderItem;
import be.loisirs.tfe2025.plateforme_loisirs.entity.OrderStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.OrderRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class StripeCheckoutService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final String stripeSecretKey;
    private final String frontendUrl;

    public StripeCheckoutService(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            @Value("${stripe.secret-key}") String stripeSecretKey,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.stripeSecretKey = stripeSecretKey;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public CheckoutResponseDTO createCheckoutSession(String userEmail, CheckoutRequestDTO request) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalStateException("La clé Stripe test n'est pas configurée.");
        }

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);

        SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment/cancel?session_id={CHECKOUT_SESSION_ID}")
                .putMetadata("userEmail", userEmail);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CheckoutItemRequestDTO itemRequest : request.getItems()) {
            Product product = getValidProduct(itemRequest);
            int quantity = itemRequest.getQuantity();

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(product.getPrice());
            order.getOrderItems().add(orderItem);

            sessionBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) quantity)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("eur")
                                            .setUnitAmount(toStripeAmount(product.getPrice()))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(product.getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.saveAndFlush(order);

        sessionBuilder.putMetadata("orderId", savedOrder.getId().toString());

        try {
            Stripe.apiKey = stripeSecretKey;
            Session session = Session.create(sessionBuilder.build());

            savedOrder.setStripeSessionId(session.getId());
            savedOrder.setStripePaymentIntentId(session.getPaymentIntent());
            orderRepository.save(savedOrder);

            return new CheckoutResponseDTO(
                    savedOrder.getId(),
                    session.getId(),
                    session.getUrl()
            );
        } catch (StripeException exception) {
            throw new IllegalStateException("Erreur lors de la création de la session Stripe.");
        }
    }

    @Transactional
    public void cancelCheckoutSession(String userEmail, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session Stripe manquante.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        Order order = orderRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable."));

        if (order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cette commande ne vous appartient pas.");
        }

        if (OrderStatus.PENDING.equals(order.getStatus())) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }

    private Product getValidProduct(CheckoutItemRequestDTO itemRequest) {
        if (itemRequest == null || itemRequest.getProductId() == null) {
            throw new IllegalArgumentException("Produit invalide.");
        }

        if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantité invalide.");
        }

        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable."));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalArgumentException("Produit inactif.");
        }

        if (product.getStockQuantity() != null && product.getStockQuantity() < itemRequest.getQuantity()) {
            throw new IllegalArgumentException("Stock insuffisant pour le produit : " + product.getName());
        }

        return product;
    }

    private Long toStripeAmount(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}