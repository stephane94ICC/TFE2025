package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import be.loisirs.tfe2025.plateforme_loisirs.util.PasswordPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDTO {

    @NotBlank(message = "Le mot de passe actuel est obligatoire.")
    private String currentPassword;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
    @Pattern(regexp = PasswordPolicy.PATTERN, message = PasswordPolicy.MESSAGE)
    private String newPassword;
}