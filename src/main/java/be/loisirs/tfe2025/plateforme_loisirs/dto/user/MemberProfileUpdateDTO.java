package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateDTO {

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
    private String lastName;

    @Pattern(
            regexp = "^$|^\\+?[0-9 ./-]{6,20}$",
            message = "Le numéro de téléphone n'est pas valide."
    )
    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères.")
    private String phone;
}