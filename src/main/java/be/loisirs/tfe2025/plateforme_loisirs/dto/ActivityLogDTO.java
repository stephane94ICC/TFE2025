package be.loisirs.tfe2025.plateforme_loisirs.dto;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDTO {

    private Long id;

    private ActivityEventType eventType;

    private Long userId;

    private String userEmail;

    private String targetType;

    private Long targetId;

    private String details;

    private String ipAddress;

    private LocalDateTime createdAt;
}
