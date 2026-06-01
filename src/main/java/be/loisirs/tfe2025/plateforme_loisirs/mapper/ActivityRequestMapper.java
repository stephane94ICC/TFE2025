package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.AdminActivityRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.activity.PartnerActivityRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityRequestMapper {

    public Activity toEntity(AdminActivityRequestDTO dto) {
        Activity activity = new Activity();
        updateEntity(dto, activity);
        return activity;
    }

    public Activity toEntity(PartnerActivityRequestDTO dto) {
        Activity activity = new Activity();
        updateEntity(dto, activity);
        return activity;
    }

    public void updateEntity(AdminActivityRequestDTO dto, Activity activity) {
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setPrice(dto.getPrice());
        activity.setDurationMinutes(dto.getDurationMinutes());
        activity.setMinimumAge(dto.getMinimumAge());
        activity.setEquipmentInformation(dto.getEquipmentInformation());
    }

    public void updateEntity(PartnerActivityRequestDTO dto, Activity activity) {
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setPrice(dto.getPrice());
        activity.setDurationMinutes(dto.getDurationMinutes());
        activity.setMinimumAge(dto.getMinimumAge());
        activity.setEquipmentInformation(dto.getEquipmentInformation());
    }
}
