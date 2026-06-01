package be.loisirs.tfe2025.plateforme_loisirs.dto.activity;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityResponseDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Integer minimumAge;
    private String equipmentInformation;
    private ActivityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    private String reviewComment;
    private Long reviewedByUserId;
    private String reviewedByUserEmail;
    private Long partnerId;
    private String partnerName;
    private List<String> imageUrls = new ArrayList<>();
}
