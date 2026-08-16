package be.loisirs.tfe2025.plateforme_loisirs.dto.reservation;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySessionResponseDTO {

    private Long id;
    private Long activityId;
    private Long locationId;
    private String locationName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer capacity;
    private Integer remainingSeats;
    private ActivitySessionStatus status;
    private LocalDateTime bookingDeadline;
}