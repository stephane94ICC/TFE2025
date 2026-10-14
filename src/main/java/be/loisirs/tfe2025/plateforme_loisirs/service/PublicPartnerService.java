package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.location.PublicLocationDTO.LinkedActivity;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.PublicPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.entity.AddressType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.PublicPartnerMapper;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLocationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivitySessionRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.AddressRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicPartnerService {

    private final PartnerRepository partnerRepository;
    private final AddressRepository addressRepository;
    private final ActivityLocationRepository activityLocationRepository;
    private final ActivitySessionRepository activitySessionRepository;
    private final PublicPartnerMapper publicPartnerMapper;

    public PublicPartnerService(
            PartnerRepository partnerRepository,
            AddressRepository addressRepository,
            ActivityLocationRepository activityLocationRepository,
            ActivitySessionRepository activitySessionRepository,
            PublicPartnerMapper publicPartnerMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.addressRepository = addressRepository;
        this.activityLocationRepository = activityLocationRepository;
        this.activitySessionRepository = activitySessionRepository;
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

        List<ActivityLocation> locations =
                activityLocationRepository.findAllByPartner_IdAndActiveTrueOrderByNameAsc(partner.getId());

        return publicPartnerMapper.toPublicDTO(
                partner, contactAddresses, locations, upcomingActivitiesByLocation(partner.getId()));
    }

    // Une seule requête pour tous les lieux, puis regroupement en mémoire (pas de requête par lieu).
    private Map<Long, List<LinkedActivity>> upcomingActivitiesByLocation(Long partnerId) {
        Map<Long, List<LinkedActivity>> result = new HashMap<>();

        for (Object[] row : activitySessionRepository.findUpcomingActivitiesByLocation(
                partnerId, ActivityStatus.APPROVED, ActivitySessionStatus.SCHEDULED, LocalDateTime.now())) {
            result.computeIfAbsent((Long) row[0], id -> new ArrayList<>())
                    .add(new LinkedActivity((Long) row[1], (String) row[2]));
        }

        return result;
    }
}