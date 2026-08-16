package be.loisirs.tfe2025.plateforme_loisirs.dto.reservation;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {

    private Long id;
    private String reference;
    private Long sessionId;
    private String activityTitle;
    private LocalDateTime sessionStartAt;
    private Integer quantity;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime bookedAt;
}