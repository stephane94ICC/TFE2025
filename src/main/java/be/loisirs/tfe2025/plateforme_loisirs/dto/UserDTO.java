package be.loisirs.tfe2025.plateforme_loisirs.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    // ========================
    // Getters / Setters
    // ========================
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean consentRgpd;
    private String password; // uniquement pour POST/PUT, jamais renvoyé au front via GET

    public UserDTO() {}

    public UserDTO(Long id, String email, String firstName, String lastName, Boolean consentRgpd) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.consentRgpd = consentRgpd;
    }

}