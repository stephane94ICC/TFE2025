package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AddressRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AddressResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponseDTO toDTO(Address address) {
        if (address == null) return null;

        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(address.getId());
        dto.setPartnerId(address.getPartner().getId());
        dto.setStreet(address.getStreet());
        dto.setHouseNumber(address.getHouseNumber());
        dto.setBox(address.getBox());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setAddressType(address.getAddressType());

        return dto;
    }

    public void updateEntity(AddressRequestDTO dto, Address address) {
        if (dto.getStreet() != null) address.setStreet(dto.getStreet());
        if (dto.getHouseNumber() != null) address.setHouseNumber(dto.getHouseNumber());
        if (dto.getBox() != null) address.setBox(dto.getBox());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getPostalCode() != null) address.setPostalCode(dto.getPostalCode());
        if (dto.getCountry() != null) address.setCountry(dto.getCountry());
        if (dto.getAddressType() != null) address.setAddressType(dto.getAddressType());
    }
}