package be.loisirs.tfe2025.plateforme_loisirs.dto.reservation;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySessionRequestDTO {

    private Long locationId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer capacity;
    private LocalDateTime bookingDeadline;
}