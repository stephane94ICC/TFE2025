package be.loisirs.tfe2025.plateforme_loisirs.security;

import be.loisirs.tfe2025.plateforme_loisirs.common.AbstractIntegrationTest;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.JwtService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Cookie de connexion et version de session")
class AuthCookieIntegrationTest extends AbstractIntegrationTest {

    private static final String PROFILE_URL = "/api/member/profile";

    @Autowired
    private JwtService jwtService;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Connexion : cookie HttpOnly, Secure, SameSite=Lax, Path=/, et aucun jeton dans le JSON")
    void loginSetsHardenedCookieWithoutTokenInBody() throws Exception {
        User member = createMember("cookie-attributes@belloisirs.test");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(withCsrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", member.getEmail(),
                                "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.expiresAt").exists())
                .andReturn();

        MockCookie authCookie = (MockCookie) result.getResponse().getCookie(authCookieName());

        assertThat(authCookie).isNotNull();
        assertThat(authCookie.getName()).isEqualTo("__Host-BL_AUTH");
        assertThat(authCookie.isHttpOnly()).isTrue();
        assertThat(authCookie.getSecure()).isTrue();
        assertThat(authCookie.getSameSite()).isEqualTo("Lax");
        assertThat(authCookie.getPath()).isEqualTo("/");
        assertThat(authCookie.getDomain()).isNull();
        assertThat(authCookie.getMaxAge()).isEqualTo((int) jwtService.getExpiration().toSeconds());
    }

    @Test
    @DisplayName("Le cookie seul suffit : aucun en-tête Authorization nécessaire")
    void cookieAloneAuthenticates() throws Exception {
        User member = createMember("cookie-only@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(token)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Déconnexion : cookie effacé et jetons refusés sur tous les appareils")
    void logoutRevokesTokensOnAllDevices() throws Exception {
        User member = createMember("cookie-logout@belloisirs.test");
        String laptopToken = tokenFor(member.getEmail());
        String phoneToken = tokenFor(member.getEmail());

        mockMvc.perform(post("/api/auth/logout").cookie(authCookie(laptopToken)).with(withCsrf()))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(authCookieName(), 0));

        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(laptopToken)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(phoneToken)))
                .andExpect(status().isUnauthorized());

        // Une nouvelle connexion porte la nouvelle version : elle fonctionne.
        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(tokenFor(member.getEmail()))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Déconnexion sans cookie : 204, rien à révoquer")
    void anonymousLogoutJustClearsCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout").with(withCsrf()))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(authCookieName(), 0));
    }

    @Test
    @DisplayName("Rôle retiré en base : refusé dès la requête suivante, avec le même jeton")
    void removedRoleTakesEffectImmediately() throws Exception {
        User member = createMember("cookie-role@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(token)))
                .andExpect(status().isOk());

        member.setRoles(new HashSet<>());
        userRepository.saveAndFlush(member);
        entityManager.clear();

        mockMvc.perform(get(PROFILE_URL).cookie(authCookie(token)))
                .andExpect(status().isForbidden());
    }
}
