package be.loisirs.tfe2025.plateforme_loisirs.dto;

public class UserDTO {

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

    // ========================
    // Getters / Setters
    // ========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getConsentRgpd() { return consentRgpd; }
    public void setConsentRgpd(Boolean consentRgpd) { this.consentRgpd = consentRgpd; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}