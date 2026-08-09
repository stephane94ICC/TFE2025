package be.loisirs.tfe2025.plateforme_loisirs.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    private String firstName;

    private String lastName;

    private String email;

    private Boolean consentRgpd;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{12,}$",
            message = "Le mot de passe doit contenir au moins 12 caractères, avec une majuscule, une minuscule, un chiffre et un caractère spécial."
    )
    private String password;
}