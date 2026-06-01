package be.loisirs.tfe2025.plateforme_loisirs.dto;

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
public class ActivityDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Integer minimumAge;
    private String equipmentInformation;
    private Boolean active;
    private LocalDateTime createdAt;
    private Long partnerId;
    private List<String> imageUrls = new ArrayList<>();
}