package be.loisirs.tfe2025.plateforme_loisirs.api.controller.reservation;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ReservationCheckoutRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ReservationCheckoutResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.ReservationCheckoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/member/reservations/checkout")
public class ReservationCheckoutController {

    private final ReservationCheckoutService reservationCheckoutService;

    public ReservationCheckoutController(ReservationCheckoutService reservationCheckoutService) {
        this.reservationCheckoutService = reservationCheckoutService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<ReservationCheckoutResponseDTO> createCheckoutSession(
            @RequestBody ReservationCheckoutRequestDTO request,
            Principal principal
    ) {
        ReservationCheckoutResponseDTO response = reservationCheckoutService.createCheckoutSession(
                principal.getName(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Void> cancelCheckoutSession(
            @RequestParam("session_id") String sessionId,
            Principal principal
    ) {
        reservationCheckoutService.cancelCheckoutSession(
                principal.getName(),
                sessionId
        );

        return ResponseEntity.noContent().build();
    }
}