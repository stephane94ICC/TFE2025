package be.loisirs.tfe2025.plateforme_loisirs.dto.partner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicPartnerResponseDTO {

    private String name;
    private String slug;
    private String description;
    private String logoUrl;

    private String website;
    private String phone;
    private String email;
    private String enterpriseNumber;

    private List<PublicAddress> addresses = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicAddress {
        private String type;
        private String street;
        private String houseNumber;
        private String box;
        private String postalCode;
        private String city;
        private String country;
    }
}