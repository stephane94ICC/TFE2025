package be.loisirs.tfe2025.plateforme_loisirs.dto.activity;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivityReviewRequestDTO {

    @NotNull(message = "Le statut est obligatoire.")
    private ActivityStatus status;

    private String reviewComment;
}
