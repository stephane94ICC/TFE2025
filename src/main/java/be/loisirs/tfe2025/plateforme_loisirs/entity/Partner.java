package be.loisirs.tfe2025.plateforme_loisirs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partner {

    public static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("5.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String phone;
    private String email;
    private String website;

    @Column(name = "enterprise_number", nullable = false, unique = true)
    private String enterpriseNumber;

    @Column(name = "vat_number", nullable = false, unique = true)
    private String vatNumber;

    @Column(name = "logo_url", nullable = false)
    private String logoUrl;

    @Column(nullable = false)
    private Boolean active = true;

    // Compte Stripe Connect : NULL tant que le partenaire n'a pas commencé son inscription.
    @Column(name = "stripe_account_id", unique = true)
    private String stripeAccountId;

    // Taux du contrat, fixé par l'administrateur. 5 % = taux standard (règle métier).
    @Column(name = "commission_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal commissionRate = DEFAULT_COMMISSION_RATE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (logoUrl == null || logoUrl.isBlank()) {
            logoUrl = "/uploads/partners/default-logo.png";
        }
        if (active == null) active = true;
        if (commissionRate == null) commissionRate = DEFAULT_COMMISSION_RATE;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}