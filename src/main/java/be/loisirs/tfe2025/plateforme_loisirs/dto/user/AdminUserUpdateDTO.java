package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import be.loisirs.tfe2025.plateforme_loisirs.util.PasswordPolicy;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminUserUpdateDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String role;
    @Pattern(regexp = PasswordPolicy.PATTERN, message = PasswordPolicy.MESSAGE)    private String password;
}
