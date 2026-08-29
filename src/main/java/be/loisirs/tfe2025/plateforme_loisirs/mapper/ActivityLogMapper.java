package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ActivityLogDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLog;

public class ActivityLogMapper {

    public static ActivityLogDTO toDTO(ActivityLog entry) {
        if (entry == null) {
            return null;
        }

        return new ActivityLogDTO(
                entry.getId(),
                entry.getEventType(),
                entry.getUserId(),
                entry.getUserEmail(),
                entry.getTargetType(),
                entry.getTargetId(),
                entry.getDetails(),
                entry.getIpAddress(),
                entry.getCreatedAt()
        );
    }
}