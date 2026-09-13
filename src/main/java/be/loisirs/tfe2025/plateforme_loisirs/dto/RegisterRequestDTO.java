package be.loisirs.tfe2025.plateforme_loisirs.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RegisterRequestDTO {

    @NotBlank(message = "Le prénom est obligatoire.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    private String lastName;

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "L'adresse e-mail doit être valide.")
    @Size(max = 255, message = "L'adresse e-mail ne peut pas dépasser 255 caractères.")
    private String email;

    @NotNull(message = "Le consentement RGPD est obligatoire.")
    @AssertTrue(message = "Le consentement RGPD est obligatoire.")
    private Boolean consentRgpd;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{12,}$",
            message = "Le mot de passe doit contenir au moins 12 caractères, avec une majuscule, une minuscule, un chiffre et un caractère spécial."
    )
    private String password;
}