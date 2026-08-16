package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ActivityLocationRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ActivityLocationMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import java.util.List;

@Service
public class PartnerActivityLocationService {

    private final ActivityLocationRepository activityLocationRepository;
    private final PartnerService partnerService;
    private final ActivityLocationMapper activityLocationMapper;

    public PartnerActivityLocationService(
            ActivityLocationRepository activityLocationRepository,
            PartnerService partnerService,
            ActivityLocationMapper activityLocationMapper
    ) {
        this.activityLocationRepository = activityLocationRepository;
        this.partnerService = partnerService;
        this.activityLocationMapper = activityLocationMapper;
    }

    public List<ActivityLocation> getLocations(String email) {
        return activityLocationRepository.findAllByPartner_User_Email(email);
    }

    @Transactional
    public ActivityLocation addLocation(String email, ActivityLocationRequestDTO dto) {
        Partner partner = partnerService.getPartnerByUserEmail(email);

        ActivityLocation location = new ActivityLocation();
        activityLocationMapper.updateEntity(dto, location);
        location.setPartner(partner);

        return activityLocationRepository.save(location);
    }
    @Transactional
    public ActivityLocation updateLocation(String email, Long id, ActivityLocationRequestDTO dto) {
        ActivityLocation location = activityLocationRepository
                .findByIdAndPartner_User_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu introuvable."));

        activityLocationMapper.updateEntity(dto, location);

        return activityLocationRepository.save(location);
    }
}