package be.loisirs.tfe2025.plateforme_loisirs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "minimum_age", nullable = false)
    private Integer minimumAge = 0;

    @Column(name = "equipment_information", columnDefinition = "TEXT")
    private String equipmentInformation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.PENDING_REVIEW;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedByUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @OneToMany(
            mappedBy = "activity",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ActivityImage> images = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (price == null) price = BigDecimal.ZERO;
        if (minimumAge == null) minimumAge = 0;
        if (status == null) status = ActivityStatus.PENDING_REVIEW;
        if (createdAt == null) createdAt = now;
        if (submittedAt == null) submittedAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
