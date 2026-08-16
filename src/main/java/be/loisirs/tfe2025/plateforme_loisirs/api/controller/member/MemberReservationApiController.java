package be.loisirs.tfe2025.plateforme_loisirs.api.controller.member;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.ReservationResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ReservationMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.MemberReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/member/reservations")
public class MemberReservationApiController {

    private final MemberReservationService memberReservationService;
    private final ReservationMapper reservationMapper;

    public MemberReservationApiController(
            MemberReservationService memberReservationService,
            ReservationMapper reservationMapper
    ) {
        this.memberReservationService = memberReservationService;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getReservations(Principal principal) {
        List<ReservationResponseDTO> reservations = memberReservationService
                .getReservations(principal.getName())
                .stream()
                .map(reservationMapper::toDTO)
                .toList();

        return ResponseEntity.ok(reservations);
    }
}