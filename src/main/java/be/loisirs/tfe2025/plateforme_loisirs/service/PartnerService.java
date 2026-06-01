package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final ImageStorageService imageStorageService;

    public PartnerService(
            PartnerRepository partnerRepository,
            ImageStorageService imageStorageService
    ) {
        this.partnerRepository = partnerRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public Partner getPartner(Long id) {
        return partnerRepository.findById(id).orElse(null);
    }

    public Partner getPartnerByUserEmail(String email) {
        return partnerRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Partenaire introuvable.")
                );
    }

    public Partner addPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public Partner updatePartner(Long id, Partner partnerToUpdate) {
        Partner existing = partnerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Partenaire introuvable.")
                );

        if (partnerToUpdate.getName() != null) {
            existing.setName(partnerToUpdate.getName());
        }

        if (partnerToUpdate.getDescription() != null) {
            existing.setDescription(partnerToUpdate.getDescription());
        }

        if (partnerToUpdate.getPhone() != null) {
            existing.setPhone(partnerToUpdate.getPhone());
        }

        if (partnerToUpdate.getEmail() != null) {
            existing.setEmail(partnerToUpdate.getEmail());
        }

        if (partnerToUpdate.getWebsite() != null) {
            existing.setWebsite(partnerToUpdate.getWebsite());
        }

        return partnerRepository.save(existing);
    }

    @Transactional
    public Partner updateLogo(String email, MultipartFile file) {
        Partner partner = getPartnerByUserEmail(email);

        String oldLogoUrl = partner.getLogoUrl();
        String newLogoUrl = imageStorageService.storeImage(
                file,
                "partners",
                partner.getId()
        );

        partner.setLogoUrl(newLogoUrl);

        Partner savedPartner = partnerRepository.saveAndFlush(partner);

        imageStorageService.deleteImage(oldLogoUrl);

        return savedPartner;
    }
}