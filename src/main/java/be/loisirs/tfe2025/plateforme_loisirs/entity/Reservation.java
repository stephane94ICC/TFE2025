package be.loisirs.tfe2025.plateforme_loisirs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ActivitySession session;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "vat_rate", precision = 4, scale = 2)
    private BigDecimal vatRate;

    // Commission figée à l'achat, comme le prix et la TVA.
    // Les trois sont NULL ensemble pour les ventes antérieures à Stripe Connect.
    @Column(name = "commission_rate", precision = 4, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "commission_htva", precision = 10, scale = 2)
    private BigDecimal commissionHtva;

    @Column(name = "commission_vat", precision = 10, scale = 2)
    private BigDecimal commissionVat;

    // Frais de paiement Stripe à la charge du partenaire, TVA de la plateforme à part (V19_1).
    // Au paiement : frais maximaux, provisoires. À la confirmation : frais réels lus chez Stripe.
    // Les deux sont NULL ensemble pour les ventes antérieures à ce calcul.
    @Column(name = "payment_fee_htva", precision = 10, scale = 2)
    private BigDecimal paymentFeeHtva;

    @Column(name = "payment_fee_vat", precision = 10, scale = 2)
    private BigDecimal paymentFeeVat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    // Remboursement Stripe : identifiant et date sont NULL ensemble (CHECK V14_3).
    // Une réservation remboursée est toujours CANCELLED.
    @Column(name = "stripe_refund_id", unique = true)
    private String stripeRefundId;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "billing_first_name", nullable = false)
    private String billingFirstName;

    @Column(name = "billing_last_name", nullable = false)
    private String billingLastName;

    @Column(name = "billing_email", nullable = false)
    private String billingEmail;

    // Adresse de facturation figée au paiement (V18_1), fournie par Stripe Checkout.
    // NULL pour les ventes antérieures : elles ne reçoivent qu'un justificatif, pas une facture.
    @Column(name = "billing_address_line1")
    private String billingAddressLine1;

    @Column(name = "billing_address_line2")
    private String billingAddressLine2;

    @Column(name = "billing_postal_code", length = 20)
    private String billingPostalCode;

    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Column(name = "billing_country", length = 2)
    private String billingCountry;

    @PrePersist
    public void prePersist() {
        if (this.bookedAt == null) {
            this.bookedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ReservationStatus.PENDING;
        }
    }
}