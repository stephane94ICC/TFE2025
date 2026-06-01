package be.loisirs.tfe2025.plateforme_loisirs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partner {

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (logoUrl == null || logoUrl.isBlank()) {
            logoUrl = "/uploads/partners/default-logo.png";
        }
        if (active == null) active = true;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}