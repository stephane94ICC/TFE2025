package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerUpdateDTO {

    private String name;
    private String description;
    private String phone;
    private String email;
    private String website;

}