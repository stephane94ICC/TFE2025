package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private Boolean consentRgpd;


}
