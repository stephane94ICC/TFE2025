package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponseDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String website;
    private String enterpriseNumber;
    private String vatNumber;
    private String logoUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}