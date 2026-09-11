package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Droit a l'effacement (art. 17 RGPD), de bout en bout.
 *
 * Le point le plus important du TFE : apres suppression, le compte ne
 * porte plus d'identite, mais les registres comptables la conservent
 * (art. 17(3)(b) RGPD, art. 60 C.TVA). C'est donc une PSEUDONYMISATION,
 * et ce test le prouve plutot que de l'affirmer.
 */
@DisplayName("Suppression de compte — art. 17 RGPD")
class AccountDeletionIntegrationTest extends AbstractIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Réservation à venir : suppression refusée, puis acceptée une fois annulée")
    void deletionIsBlockedUntilUpcomingReservationsAreCancelled() throws Exception {
        User member = createMember("deletion-flow@belloisirs.test");
        String originalEmail = member.getEmail();
        Long memberId = member.getId();

        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(member, session);

        String token = tokenFor(originalEmail);
        String body = "{\"password\":\"" + PASSWORD + "\"}";

        // 1. Refus : une reservation confirmee est encore a venir.
        mockMvc.perform(post("/api/member/profile/deletion")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));

        // 2. Le membre annule sa reservation.
        mockMvc.perform(patch("/api/member/reservations/{id}/cancel", reservation.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());

        // 3. La suppression est desormais acceptee.
        mockMvc.perform(post("/api/member/profile/deletion")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        // On force l'ecriture en base puis on vide le contexte de
        // persistance : les lectures suivantes repartent de MySQL et non
        // du cache Hibernate. Sans cela, on verifierait de la memoire.
        entityManager.flush();
        entityManager.clear();

        // 4. Le compte ne porte plus d'identite.
        User deleted = userRepository.findById(memberId).orElseThrow();
        assertThat(deleted.getEmail()).isEqualTo("anonyme-" + memberId + "@belloisirs.invalid");
        assertThat(deleted.getFirstName()).isEqualTo("Membre");
        assertThat(deleted.getActive()).isFalse();

        // 5. Mais l'identite de FACTURATION est conservee : obligation
        //    comptable de 10 ans. Une jointure reste donc possible :
        //    pseudonymisation, pas anonymisation au sens strict.
        Reservation invoice = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(invoice.getBillingEmail()).isEqualTo(originalEmail);
        assertThat(invoice.getBillingLastName()).isEqualTo("Test");
    }

}