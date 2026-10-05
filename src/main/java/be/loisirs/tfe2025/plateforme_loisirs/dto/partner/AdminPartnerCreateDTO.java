package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import be.loisirs.tfe2025.plateforme_loisirs.util.EnterpriseNumber;
import be.loisirs.tfe2025.plateforme_loisirs.util.PasswordPolicy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
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
public class AdminPartnerCreateDTO {

    // --- Compte de connexion ---

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "L'adresse e-mail doit être valide.")
    @Size(max = 255, message = "L'adresse e-mail ne peut pas dépasser 255 caractères.")
    private String email;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
    private String lastName;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Pattern(regexp = PasswordPolicy.PATTERN, message = PasswordPolicy.MESSAGE)
    private String password;

    // --- Fiche partenaire ---

    @NotBlank(message = "Le nom commercial est obligatoire.")
    @Size(max = 255, message = "Le nom commercial ne peut pas dépasser 255 caractères.")
    private String name;

    @NotBlank(message = "Le numéro d'entreprise est obligatoire.")
    private String enterpriseNumber;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères.")
    private String phone;

    @Email(message = "L'e-mail de contact doit être valide.")
    @Size(max = 255, message = "L'e-mail de contact ne peut pas dépasser 255 caractères.")
    private String contactEmail;

    @Size(max = 255, message = "Le site web ne peut pas dépasser 255 caractères.")
    private String website;


    @JsonIgnore
    @AssertTrue(message = EnterpriseNumber.MESSAGE)
    public boolean isEnterpriseNumberValid() {
        if (enterpriseNumber == null || enterpriseNumber.isBlank()) {
            return true;
        }
        return EnterpriseNumber.isValid(EnterpriseNumber.normalize(enterpriseNumber));
    }
}