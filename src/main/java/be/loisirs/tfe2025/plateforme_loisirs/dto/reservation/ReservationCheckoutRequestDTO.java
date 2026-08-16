package be.loisirs.tfe2025.plateforme_loisirs.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCheckoutRequestDTO {

    private Long sessionId;
    private Integer quantity;
}