package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AddressRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AddressResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.AddressMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/partner/addresses")
public class PartnerAddressApiController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    public PartnerAddressApiController(
            AddressService addressService,
            AddressMapper addressMapper
    ) {
        this.addressService = addressService;
        this.addressMapper = addressMapper;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAddresses(Principal principal) {
        List<AddressResponseDTO> addresses = addressService
                .getAddressesByUserEmail(principal.getName())
                .stream()
                .map(addressMapper::toDTO)
                .toList();

        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            @RequestBody AddressRequestDTO dto,
            Principal principal
    ) {
        Address savedAddress = addressService.addAddress(principal.getName(), dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressMapper.toDTO(savedAddress));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequestDTO dto,
            Principal principal
    ) {
        Address updatedAddress = addressService.updateAddress(
                principal.getName(),
                addressId,
                dto
        );

        return ResponseEntity.ok(addressMapper.toDTO(updatedAddress));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            Principal principal
    ) {
        addressService.deleteAddress(principal.getName(), addressId);

        return ResponseEntity.noContent().build();
    }
}