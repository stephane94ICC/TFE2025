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
        dto.setPartnerId(activity.getPartnerId());

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

        if (dto.getPartnerId() != null) {
            existing.setPartnerId(dto.getPartnerId());
        }
    }
}