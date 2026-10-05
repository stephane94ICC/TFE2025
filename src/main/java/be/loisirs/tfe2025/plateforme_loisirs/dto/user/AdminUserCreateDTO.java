package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import jakarta.validation.constraints.Pattern;
import be.loisirs.tfe2025.plateforme_loisirs.util.PasswordPolicy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class AdminUserCreateDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String role;
    @Pattern(regexp = PasswordPolicy.PATTERN, message = PasswordPolicy.MESSAGE)
    private String password;
}
