package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PartnerActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PublicActivityResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityResponseMapper {

    public PublicActivityResponseDTO toPublicDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        PublicActivityResponseDTO dto = new PublicActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setPrice(activity.getPrice());
        dto.setDurationMinutes(activity.getDurationMinutes());
        dto.setMinimumAge(activity.getMinimumAge());
        dto.setEquipmentInformation(activity.getEquipmentInformation());
        dto.setImageUrls(getImageUrls(activity));

        if (activity.getPartner() != null) {
            dto.setPartnerId(activity.getPartner().getId());
            dto.setPartnerName(activity.getPartner().getName());
        }

        return dto;
    }

    public AdminActivityResponseDTO toAdminDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        AdminActivityResponseDTO dto = new AdminActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setPrice(activity.getPrice());
        dto.setDurationMinutes(activity.getDurationMinutes());
        dto.setMinimumAge(activity.getMinimumAge());
        dto.setEquipmentInformation(activity.getEquipmentInformation());
        dto.setStatus(activity.getStatus());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setSubmittedAt(activity.getSubmittedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        dto.setReviewedAt(activity.getReviewedAt());
        dto.setReviewComment(activity.getReviewComment());
        dto.setImageUrls(getImageUrls(activity));

        if (activity.getReviewedByUser() != null) {
            dto.setReviewedByUserId(activity.getReviewedByUser().getId());
            dto.setReviewedByUserEmail(activity.getReviewedByUser().getEmail());
        }

        if (activity.getPartner() != null) {
            dto.setPartnerId(activity.getPartner().getId());
            dto.setPartnerName(activity.getPartner().getName());
        }

        return dto;
    }

    public PartnerActivityResponseDTO toPartnerDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        PartnerActivityResponseDTO dto = new PartnerActivityResponseDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setPrice(activity.getPrice());
        dto.setDurationMinutes(activity.getDurationMinutes());
        dto.setMinimumAge(activity.getMinimumAge());
        dto.setEquipmentInformation(activity.getEquipmentInformation());
        dto.setStatus(activity.getStatus());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setSubmittedAt(activity.getSubmittedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        dto.setReviewedAt(activity.getReviewedAt());
        dto.setReviewComment(activity.getReviewComment());
        dto.setImageUrls(getImageUrls(activity));

        return dto;
    }

    private List<String> getImageUrls(Activity activity) {
        if (activity.getImages() == null) {
            return List.of();
        }

        return activity.getImages()
                .stream()
                .map(image -> image.getUrl())
                .toList();
    }
}
