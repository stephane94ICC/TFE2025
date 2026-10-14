package be.loisirs.tfe2025.plateforme_loisirs.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// Lieu d'activité tel que le public le voit : adresse et accès, jamais le partenaire ni les coordonnées internes.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicLocationDTO {

    private String name;
    private String street;
    private String houseNumber;
    private String box;
    private String postalCode;
    private String city;
    private String country;
    private String accessInformation;

    // Rempli seulement sur la vitrine du partenaire ; absent du JSON des créneaux.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<LinkedActivity> activities;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkedActivity {
        private Long id;
        private String title;
    }
}