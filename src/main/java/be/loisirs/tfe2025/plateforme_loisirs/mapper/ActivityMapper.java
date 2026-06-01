package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ActivityDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityDTO toDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setPrice(activity.getPrice());
        dto.setDurationMinutes(activity.getDurationMinutes());
        dto.setMinimumAge(activity.getMinimumAge());
        dto.setEquipmentInformation(activity.getEquipmentInformation());
        dto.setActive(activity.getActive());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setPartnerId(activity.getPartnerId());

        if (activity.getImages() != null) {
            dto.setImageUrls(
                    activity.getImages()
                            .stream()
                            .map(image -> image.getUrl())
                            .toList()
            );
        }

        return dto;
    }

    public Activity toEntity(ActivityDTO dto) {
        if (dto == null) {
            return null;
        }

        Activity activity = new Activity();
        activity.setId(dto.getId());
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setPrice(dto.getPrice());
        activity.setDurationMinutes(dto.getDurationMinutes());
        activity.setMinimumAge(dto.getMinimumAge());
        activity.setEquipmentInformation(dto.getEquipmentInformation());
        activity.setActive(dto.getActive());
        activity.setCreatedAt(dto.getCreatedAt());
        activity.setPartnerId(dto.getPartnerId());

        return activity;
    }

    public void updateEntity(ActivityDTO dto, Activity existing) {
        if (dto.getTitle() != null) {
            existing.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }

        if (dto.getDurationMinutes() != null) {
            existing.setDurationMinutes(dto.getDurationMinutes());
        }

        if (dto.getMinimumAge() != null) {
            existing.setMinimumAge(dto.getMinimumAge());
        }

        if (dto.getEquipmentInformation() != null) {
            existing.setEquipmentInformation(dto.getEquipmentInformation());
        }

        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }

        if (dto.getPartnerId() != null) {
            existing.setPartnerId(dto.getPartnerId());
        }
    }
}