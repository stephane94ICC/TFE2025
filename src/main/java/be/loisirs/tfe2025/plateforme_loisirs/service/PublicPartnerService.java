package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PublicPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.entity.AddressType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.PublicPartnerMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.AddressRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicPartnerService {

    private final PartnerRepository partnerRepository;
    private final AddressRepository addressRepository;
    private final PublicPartnerMapper publicPartnerMapper;

    public PublicPartnerService(
            PartnerRepository partnerRepository,
            AddressRepository addressRepository,
            PublicPartnerMapper publicPartnerMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.addressRepository = addressRepository;
        this.publicPartnerMapper = publicPartnerMapper;
    }

    @Transactional(readOnly = true)
    public PublicPartnerResponseDTO getActivePartnerBySlug(String slug) {
        Partner partner = partnerRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Partenaire introuvable.")
                );

        List<Address> contactAddresses = addressRepository.findByPartnerIdAndAddressType(
                partner.getId(),
                AddressType.CONTACT
        );

        return publicPartnerMapper.toPublicDTO(partner, contactAddresses);
    }
}