package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Limitation des essais de connexion, de bout en bout (vraie route /api/auth/login).
 *
 * Le limiteur garde ses compteurs en memoire et le contexte Spring est partage
 * entre toutes les classes de test : chaque test utilise donc sa PROPRE adresse IP
 * et son propre e-mail, pour ne jamais heriter des echecs d'un autre test.
 * Les paliers (15 min, 1 h, oubli a 24 h) sont verifies dans LoginAttemptLimiterTest.
 */
@DisplayName("Limitation des essais de connexion (intégration)")
class LoginRateLimitIntegrationTest extends AbstractIntegrationTest {

    private static final String WRONG_PASSWORD = "MauvaisMotDePasse9!";

    private ResultActions login(String email, String password, String ip) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .with(request -> {
                    request.setRemoteAddr(ip);
                    return request;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password))));
    }

    private void failTimes(String email, String ip, int times) throws Exception {
        for (int i = 0; i < times; i++) {
            login(email, WRONG_PASSWORD, ip).andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("5 échecs : la 6e tentative est refusée (429) MÊME avec le bon mot de passe")
    void sixthAttemptIsBlockedEvenWithCorrectPassword() throws Exception {
        User member = createMember("rate-blocked@belloisirs.test");
        String ip = "10.20.0.1";

        failTimes(member.getEmail(), ip, 5);

        login(member.getEmail(), PASSWORD, ip)
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(HttpHeaders.RETRY_AFTER, "900"))
                .andExpect(jsonPath("$.code").value("TOO_MANY_LOGIN_ATTEMPTS"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @DisplayName("Le même compte depuis une autre IP n'est pas bloqué")
    void sameAccountFromAnotherIpIsNotBlocked() throws Exception {
        User member = createMember("rate-other-ip@belloisirs.test");

        failTimes(member.getEmail(), "10.20.0.2", 5);

        login(member.getEmail(), PASSWORD, "10.20.0.3")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("E-mail inconnu : bloqué de la même façon (le blocage ne révèle pas quels comptes existent)")
    void unknownEmailIsBlockedTheSameWay() throws Exception {
        String unknownEmail = "rate-inconnu@belloisirs.test";
        String ip = "10.20.0.4";

        failTimes(unknownEmail, ip, 5);

        login(unknownEmail, WRONG_PASSWORD, ip)
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Une connexion réussie remet le compteur à zéro")
    void successfulLoginResetsCounter() throws Exception {
        User member = createMember("rate-reset@belloisirs.test");
        String ip = "10.20.0.5";

        failTimes(member.getEmail(), ip, 4);
        login(member.getEmail(), PASSWORD, ip).andExpect(status().isOk());

        // Sans remise a zero, le 5e echec ci-dessous declencherait le blocage.
        failTimes(member.getEmail(), ip, 4);
        login(member.getEmail(), PASSWORD, ip).andExpect(status().isOk());
    }
}