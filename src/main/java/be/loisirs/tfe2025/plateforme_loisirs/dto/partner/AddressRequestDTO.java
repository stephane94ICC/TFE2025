package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import be.loisirs.tfe2025.plateforme_loisirs.entity.AddressType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    private String street;
    private String houseNumber;
    private String box;
    private String city;
    private String postalCode;
    private String country;
    private AddressType addressType;
}