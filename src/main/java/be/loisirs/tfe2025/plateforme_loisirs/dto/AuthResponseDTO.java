package be.loisirs.tfe2025.plateforme_loisirs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String message;
}