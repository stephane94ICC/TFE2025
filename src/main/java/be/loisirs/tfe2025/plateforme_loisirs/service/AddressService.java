package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AddressRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.AddressMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final PartnerService partnerService;
    private final AddressMapper addressMapper;

    public AddressService(
            AddressRepository addressRepository,
            PartnerService partnerService,
            AddressMapper addressMapper
    ) {
        this.addressRepository = addressRepository;
        this.partnerService = partnerService;
        this.addressMapper = addressMapper;
    }

    public List<Address> getAddressesByUserEmail(String email) {
        return addressRepository.findByPartnerUserEmail(email);
    }

    @Transactional
    public Address addAddress(String email, AddressRequestDTO dto) {
        Partner partner = partnerService.getPartnerByUserEmail(email);

        Address address = new Address();
        addressMapper.updateEntity(dto, address);
        address.setPartner(partner);

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(String email, Long addressId, AddressRequestDTO dto) {
        Address address = getOwnedAddress(email, addressId);

        addressMapper.updateEntity(dto, address);

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        Address address = getOwnedAddress(email, addressId);

        addressRepository.delete(address);
    }

    private Address getOwnedAddress(String email, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse introuvable."));

        if (!address.getPartner().getUser().getEmail().equals(email)) {
            throw new ResourceNotFoundException("Adresse introuvable.");
        }

        return address;
    }
}