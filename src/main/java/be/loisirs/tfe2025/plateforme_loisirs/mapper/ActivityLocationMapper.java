package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivityLocationRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivityLocationResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import org.springframework.stereotype.Component;

@Component
public class ActivityLocationMapper {

    public ActivityLocationResponseDTO toDTO(ActivityLocation location) {
        if (location == null) return null;

        ActivityLocationResponseDTO dto = new ActivityLocationResponseDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setStreet(location.getStreet());
        dto.setHouseNumber(location.getHouseNumber());
        dto.setBox(location.getBox());
        dto.setCity(location.getCity());
        dto.setPostalCode(location.getPostalCode());
        dto.setCountry(location.getCountry());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setAccessInformation(location.getAccessInformation());

        return dto;
    }

    public void updateEntity(ActivityLocationRequestDTO dto, ActivityLocation location) {
        if (dto.getName() != null) location.setName(dto.getName());
        if (dto.getStreet() != null) location.setStreet(dto.getStreet());
        if (dto.getHouseNumber() != null) location.setHouseNumber(dto.getHouseNumber());
        if (dto.getBox() != null) location.setBox(dto.getBox());
        if (dto.getCity() != null) location.setCity(dto.getCity());
        if (dto.getPostalCode() != null) location.setPostalCode(dto.getPostalCode());
        if (dto.getCountry() != null) location.setCountry(dto.getCountry());
        if (dto.getLatitude() != null) location.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) location.setLongitude(dto.getLongitude());
        if (dto.getAccessInformation() != null) location.setAccessInformation(dto.getAccessInformation());
    }
}