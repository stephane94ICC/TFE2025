package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeletionRequestDTO {

    @NotBlank(message = "Le mot de passe est obligatoire pour confirmer la suppression.")
    private String password;
}