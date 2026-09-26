package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ReservationCheckoutRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ReservationCheckoutResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivitySessionRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationCheckoutService {

    private final UserRepository userRepository;
    private final ActivitySessionRepository activitySessionRepository;
    private final ReservationRepository reservationRepository;
    private final ActivityLogService activityLogService;
    private final String stripeSecretKey;
    private final String frontendUrl;
    private final long checkoutExpirationMinutes;

    public ReservationCheckoutService(
            UserRepository userRepository,
            ActivitySessionRepository activitySessionRepository,
            ReservationRepository reservationRepository,
            ActivityLogService activityLogService,
            @Value("${stripe.secret-key}") String stripeSecretKey,
            @Value("${app.frontend-url}") String frontendUrl,
            @Value("${stripe.checkout-expiration-minutes}") long checkoutExpirationMinutes
    ) {
        this.userRepository = userRepository;
        this.activitySessionRepository = activitySessionRepository;
        this.reservationRepository = reservationRepository;
        this.activityLogService = activityLogService;
        this.stripeSecretKey = stripeSecretKey;
        this.frontendUrl = frontendUrl;
        this.checkoutExpirationMinutes = checkoutExpirationMinutes;
    }

    @Transactional
    public ReservationCheckoutResponseDTO createCheckoutSession(
            String userEmail,
            ReservationCheckoutRequestDTO request
    ) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalStateException("La clé Stripe test n'est pas configurée.");
        }

        if (request == null || request.getSessionId() == null) {
            throw new IllegalArgumentException("Créneau manquant.");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantité invalide.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        ActivitySession session = activitySessionRepository
                .findByIdAndStatus(request.getSessionId(), ActivitySessionStatus.SCHEDULED)
                .orElseThrow(() -> new IllegalArgumentException("Créneau introuvable ou indisponible."));

        Activity activity = session.getActivity();

        if (!ActivityStatus.APPROVED.equals(activity.getStatus())) {
            throw new IllegalArgumentException("Activité non disponible.");
        }

        if (LocalDateTime.now().isAfter(session.getBookingDeadline())) {
            throw new IllegalArgumentException("Le délai de réservation pour ce créneau est dépassé.");
        }

        int alreadyBooked = reservationRepository.sumQuantityBySessionIdAndStatusIn(
                session.getId(),
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        int remainingSeats = session.getCapacity() - alreadyBooked;

        if (request.getQuantity() > remainingSeats) {
            throw new IllegalArgumentException(
                    "Places insuffisantes : " + remainingSeats + " place(s) restante(s)."
            );
        }

        BigDecimal totalPrice = activity.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setSession(session);
        reservation.setQuantity(request.getQuantity());
        reservation.setTotalPrice(totalPrice);
        reservation.setVatRate(activity.getVatRate());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReference("TEMP-" + UUID.randomUUID());
        reservation.setBillingFirstName(user.getFirstName());
        reservation.setBillingLastName(user.getLastName());
        reservation.setBillingEmail(user.getEmail());

        Reservation savedReservation = reservationRepository.saveAndFlush(reservation);

        String reference = "RES-" + LocalDateTime.now().getYear()
                + "-" + String.format("%03d", savedReservation.getId());
        savedReservation.setReference(reference);

        SessionCreateParams sessionParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/payment/success?type=reservation&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment/cancel?type=reservation&session_id={CHECKOUT_SESSION_ID}")
                .setExpiresAt(Instant.now().plusSeconds(checkoutExpirationMinutes * 60L).getEpochSecond())
                .putMetadata("userEmail", userEmail)
                .putMetadata("reservationId", savedReservation.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) request.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(toStripeAmount(activity.getPrice()))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(activity.getTitle())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            Stripe.apiKey = stripeSecretKey;
            Session stripeSession = Session.create(sessionParams);

            savedReservation.setStripeSessionId(stripeSession.getId());
            // Pas de payment intent ici : Stripe ne le crée qu'au paiement.
            // Il est enregistré par le webhook, seule source fiable.
            reservationRepository.save(savedReservation);

            /*
             * Journalisation volontairement placée après la création réussie
             * de la session Stripe, et non juste après saveAndFlush.
             *
             * Le journal écrit dans sa propre transaction (REQUIRES_NEW) :
             * son entrée survit à l'annulation de la transaction appelante.
             * Journaliser plus haut produirait donc une entrée orpheline,
             * pointant vers une réservation effacée par le rollback si
             * Session.create venait à échouer.
             */
            activityLogService.log(
                    ActivityEventType.RESERVATION_CREATED,
                    "Reservation",
                    savedReservation.getId(),
                    savedReservation.getReference()
                            + " - " + activity.getTitle()
                            + " - " + request.getQuantity() + " place(s)"
                            + " - " + totalPrice + " EUR"
                            + " - en attente de paiement"
            );

            return new ReservationCheckoutResponseDTO(
                    savedReservation.getId(),
                    savedReservation.getReference(),
                    stripeSession.getId(),
                    stripeSession.getUrl()
            );
        } catch (StripeException exception) {
            throw new IllegalStateException("Erreur lors de la création de la session Stripe.");
        }
    }

    /*
     * Appelée quand le client revient de Stripe par le lien « retour ».
     * La session Stripe reste ouverte tant qu'elle n'a pas expiré :
     * annuler seulement en base laisserait le client payer par un retour
     * arrière du navigateur. On expire donc la session chez Stripe avant
     * de libérer les places.
     */
    @Transactional
    public void cancelCheckoutSession(String userEmail, String stripeSessionId) {
        if (stripeSessionId == null || stripeSessionId.isBlank()) {
            throw new IllegalArgumentException("Session Stripe manquante.");
        }

        // Même réponse pour « inexistante » et « appartient à un autre » :
        // l'existence d'une réservation n'est jamais révélée (anti-IDOR).
        Reservation reservation = reservationRepository.findByStripeSessionId(stripeSessionId)
                .filter(found -> found.getUser() != null
                        && userEmail.equals(found.getUser().getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        // Déjà confirmée ou déjà annulée : rien à faire (rechargement de la page).
        if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
            return;
        }

        String stripeStatus = expireStripeSessionIfOpen(stripeSessionId);

        if ("complete".equals(stripeStatus)) {
            throw new IllegalArgumentException(
                    "Le paiement a déjà été reçu : la réservation ne peut plus être annulée ici.");
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
                        + " - paiement abandonné par le client"
        );
    }

    /*
     * Renvoie l'état de la session Stripe après l'opération :
     * « expired » (expirée maintenant ou avant) ou « complete » (déjà payée).
     */
    private String expireStripeSessionIfOpen(String stripeSessionId) {
        try {
            Stripe.apiKey = stripeSecretKey;
            Session stripeSession = Session.retrieve(stripeSessionId);

            if ("open".equals(stripeSession.getStatus())) {
                stripeSession = stripeSession.expire();
            }

            return stripeSession.getStatus();
        } catch (StripeException exception) {
            throw new IllegalStateException(
                    "Impossible de vérifier la session de paiement auprès de Stripe.");
        }
    }

    private Long toStripeAmount(BigDecimal amount) {
        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}