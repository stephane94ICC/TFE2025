package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PublicPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PublicPartnerResponseDTO.PublicAddress;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicPartnerMapper {

    public PublicPartnerResponseDTO toPublicDTO(Partner partner, List<Address> addresses) {
        if (partner == null) {
            return null;
        }

        PublicPartnerResponseDTO dto = new PublicPartnerResponseDTO();
        dto.setName(partner.getName());
        dto.setSlug(partner.getSlug());
        dto.setDescription(partner.getDescription());
        dto.setLogoUrl(partner.getLogoUrl());
        dto.setWebsite(partner.getWebsite());
        dto.setPhone(partner.getPhone());
        dto.setEmail(partner.getEmail());
        dto.setEnterpriseNumber(partner.getEnterpriseNumber());

        if (addresses != null) {
            dto.setAddresses(addresses.stream().map(this::toPublicAddress).toList());
        }

        return dto;
    }

    private PublicAddress toPublicAddress(Address address) {
        return new PublicAddress(
                address.getAddressType() != null ? address.getAddressType().name() : null,
                address.getStreet(),
                address.getHouseNumber(),
                address.getBox(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }
}