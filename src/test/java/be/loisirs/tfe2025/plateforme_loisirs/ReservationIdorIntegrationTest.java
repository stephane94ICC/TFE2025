package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Reference directe non securisee (IDOR).
 *
 * Les deux tests se tiennent mutuellement : un findByIdAndUser_Email
 * casse renverrait 404 dans TOUS les cas. Le test de securite passerait
 * au vert en ayant detruit la fonctionnalite.
 */
@DisplayName("IDOR — annulation de réservation")
class ReservationIdorIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("La réservation d'autrui renvoie 404, jamais 403")
    void otherMemberReservationIsNotFound() throws Exception {
        User owner = createMember("idor-owner@belloisirs.test");
        User intruder = createMember("idor-intruder@belloisirs.test");

        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(owner, session);

        String intruderToken = tokenFor(intruder.getEmail());

        // 404 et non 403 : un 403 confirmerait l'existence de la ressource
        // et permettrait d'enumerer les identifiants.
        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(intruderToken)))
                .andExpect(status().isNotFound());

        // La reservation de la victime est intacte.
        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(after.getCancelledAt()).isNull();
    }

    @Test
    @DisplayName("Sa propre réservation s'annule bien : 204")
    void ownReservationIsCancelled() throws Exception {
        User member = createMember("idor-nominal@belloisirs.test");

        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(member, session);

        String token = tokenFor(member.getEmail());

        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());

        Reservation after = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(after.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(after.getCancelledAt()).isNotNull();
    }

}