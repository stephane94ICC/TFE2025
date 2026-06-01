package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PartnerUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import org.springframework.stereotype.Component;

@Component
public class PartnerMapper {

    public PartnerResponseDTO toDTO(Partner partner) {
        if (partner == null) {
            return null;
        }

        PartnerResponseDTO dto = new PartnerResponseDTO();
        dto.setId(partner.getId());

        if (partner.getUser() != null) {
            dto.setUserId(partner.getUser().getId());
            dto.setUserEmail(partner.getUser().getEmail());
        }

        dto.setName(partner.getName());
        dto.setDescription(partner.getDescription());
        dto.setPhone(partner.getPhone());
        dto.setEmail(partner.getEmail());
        dto.setWebsite(partner.getWebsite());
        dto.setEnterpriseNumber(partner.getEnterpriseNumber());
        dto.setVatNumber(partner.getVatNumber());
        dto.setLogoUrl(partner.getLogoUrl());
        dto.setActive(partner.getActive());
        dto.setCreatedAt(partner.getCreatedAt());

        return dto;
    }

    public void updateEntity(PartnerUpdateDTO dto, Partner partner) {
        if (dto == null || partner == null) {
            return;
        }

        if (dto.getName() != null) {
            partner.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            partner.setDescription(dto.getDescription());
        }

        if (dto.getPhone() != null) {
            partner.setPhone(dto.getPhone());
        }

        if (dto.getEmail() != null) {
            partner.setEmail(dto.getEmail());
        }

        if (dto.getWebsite() != null) {
            partner.setWebsite(dto.getWebsite());
        }
    }
}