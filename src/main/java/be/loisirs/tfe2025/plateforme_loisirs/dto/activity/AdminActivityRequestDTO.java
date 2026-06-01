package be.loisirs.tfe2025.plateforme_loisirs.dto.activity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityRequestDTO {

    @NotBlank(message = "Le titre est obligatoire.")
    private String title;

    private String description;

    @NotNull(message = "Le prix est obligatoire.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix ne peut pas être négatif.")
    private BigDecimal price;

    @NotNull(message = "La durée est obligatoire.")
    @Positive(message = "La durée doit être supérieure à zéro.")
    private Integer durationMinutes;

    @NotNull(message = "L'âge minimum est obligatoire.")
    @PositiveOrZero(message = "L'âge minimum ne peut pas être négatif.")
    private Integer minimumAge;

    private String equipmentInformation;

    @NotNull(message = "L'entreprise partenaire est obligatoire.")
    private Long partnerId;
}
