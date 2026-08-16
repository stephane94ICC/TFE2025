package be.loisirs.tfe2025.plateforme_loisirs.dto.reservation;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLocationRequestDTO {

    private String name;
    private String street;
    private String houseNumber;
    private String box;
    private String city;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String accessInformation;
}