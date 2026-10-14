package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.location.PublicLocationDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import org.springframework.stereotype.Component;

@Component
public class PublicLocationMapper {

    public PublicLocationDTO toPublicDTO(ActivityLocation location) {
        if (location == null) {
            return null;
        }

        PublicLocationDTO dto = new PublicLocationDTO();
        dto.setName(location.getName());
        dto.setStreet(location.getStreet());
        dto.setHouseNumber(location.getHouseNumber());
        dto.setBox(location.getBox());
        dto.setPostalCode(location.getPostalCode());
        dto.setCity(location.getCity());
        dto.setCountry(location.getCountry());
        dto.setAccessInformation(location.getAccessInformation());

        return dto;
    }
}