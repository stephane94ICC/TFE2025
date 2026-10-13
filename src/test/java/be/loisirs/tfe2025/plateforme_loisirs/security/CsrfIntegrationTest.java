package be.loisirs.tfe2025.plateforme_loisirs.security;

import be.loisirs.tfe2025.plateforme_loisirs.common.AbstractIntegrationTest;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Protection CSRF (double soumission par cookie) et réponse 401")
class CsrfIntegrationTest extends AbstractIntegrationTest {

    private static final String PROFILE_URL = "/api/member/profile";
    private static final String LOGOUT_URL = "/api/auth/logout";

    @Test
    @DisplayName("Première requête : cookie XSRF-TOKEN posé, lisible par le JavaScript")
    void firstRequestSetsReadableCsrfCookie() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(cookie().httpOnly("XSRF-TOKEN", false));
    }

    @Test
    @DisplayName("Requête par cookie sans jeton CSRF : refusée (403) et sans aucun effet")
    void cookieRequestWithoutCsrfTokenIsRejected() throws Exception {
        User member = createMember("csrf-missing@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(post(LOGOUT_URL).cookie(authCookie(token)))
                .andExpect(status().isForbidden());

        // La déconnexion forgée n'a pas eu lieu : la session est intacte.
        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(token)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Jeton CSRF différent de celui du cookie : refusé")
    void mismatchedCsrfTokenIsRejected() throws Exception {
        User member = createMember("csrf-mismatch@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(post(LOGOUT_URL)
                        .cookie(authCookie(token), new Cookie("XSRF-TOKEN", "valeur-du-cookie"))
                        .header("X-XSRF-TOKEN", "autre-valeur"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Même jeton dans le cookie et l'en-tête, comme axios : accepté")
    void matchingCsrfTokenIsAccepted() throws Exception {
        User member = createMember("csrf-ok@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(post(LOGOUT_URL).cookie(authCookie(token)).with(withCsrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("En-tête Bearer : pas de jeton CSRF exigé")
    void bearerRequestIsExemptFromCsrf() throws Exception {
        User member = createMember("csrf-bearer@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(post(LOGOUT_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Connexion sans jeton CSRF : refusée (protection contre le « login CSRF »)")
    void loginWithoutCsrfTokenIsRejected() throws Exception {
        User member = createMember("csrf-login@belloisirs.test");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", member.getEmail(),
                                "password", PASSWORD))))
                .andExpect(status().isForbidden())
                .andExpect(cookie().doesNotExist(authCookieName()));
    }

    @Test
    @DisplayName("Anonyme sur une route protégée : 401")
    void anonymousRequestGetsUnauthorized() throws Exception {
        mockMvc.perform(get(PROFILE_URL))
                .andExpect(status().isUnauthorized());
    }
}