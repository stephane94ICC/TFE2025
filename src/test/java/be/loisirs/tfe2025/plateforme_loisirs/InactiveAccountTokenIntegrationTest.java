package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Revalidation de l'etat du compte a chaque requete.
 *
 * Un JWT est autoporteur : une fois emis, il reste cryptographiquement
 * valide jusqu'a son expiration. Sans revalidation cote serveur, un
 * compte desactive continuerait d'acceder au systeme avec son ancien
 * jeton. C'est ce que ce test interdit.
 */
@DisplayName("Jeton d'un compte désactivé")
class InactiveAccountTokenIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Un jeton valide devient inopérant dès que le compte est désactivé")
    void tokenStopsWorkingOnceAccountIsDeactivated() throws Exception {
        User member = createMember("inactive-token@belloisirs.test");
        String token = tokenFor(member.getEmail());

        // 1. Le jeton fonctionne. Sans cette etape, le 403 qui suit
        //    pourrait vouloir dire "ce jeton n'a jamais ete valide".
        mockMvc.perform(get("/api/member/profile")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk());

        // 2. Desactivation du compte. Le jeton, lui, ne change pas :
        //    il reste signe et non expire.
        member.setActive(false);
        userRepository.save(member);

        // 3. Le MEME jeton est desormais refuse.
        mockMvc.perform(get("/api/member/profile")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isForbidden());
    }

}