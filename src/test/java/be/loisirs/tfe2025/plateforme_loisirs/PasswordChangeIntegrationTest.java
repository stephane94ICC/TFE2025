package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Changement de mot de passe par l'utilisateur connecte : PUT /api/member/profile/password.
 *
 * Le cas nominal ne se contente pas du 204 : il prouve, apres flush/clear,
 * que la NOUVELLE empreinte est bien en base (connexion reussie avec le
 * nouveau mot de passe) et que l'ANCIEN ne fonctionne plus.
 * Les cas d'erreur prouvent que le mot de passe n'a pas change.
 */
@DisplayName("Changement de mot de passe")
class PasswordChangeIntegrationTest extends AbstractIntegrationTest {

    private static final String URL = "/api/member/profile/password";
    private static final String NEW_PASSWORD = "NouveauMotDePasse2?";

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Mot de passe actuel correct : 204, le nouveau fonctionne, l'ancien est refusé")
    void changesPasswordAndOnlyNewOneWorks() throws Exception {
        User member = createMember("pwd-ok@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(put(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isNoContent());

        // Lire MySQL, pas le cache Hibernate.
        entityManager.flush();
        entityManager.clear();

        login(member.getEmail(), NEW_PASSWORD).andExpect(status().isOk());
        login(member.getEmail(), PASSWORD).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Mot de passe actuel incorrect : 400 et mot de passe inchangé")
    void wrongCurrentPasswordIsRejected() throws Exception {
        User member = createMember("pwd-wrong@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(put(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("MauvaisMotDePasse9!", NEW_PASSWORD)))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();

        login(member.getEmail(), PASSWORD).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Nouveau mot de passe trop faible : 400 (même règle qu'à l'inscription)")
    void weakNewPasswordIsRejected() throws Exception {
        User member = createMember("pwd-weak@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(put(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(PASSWORD, "faible")))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();

        login(member.getEmail(), PASSWORD).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Nouveau mot de passe identique à l'actuel : 400")
    void sameAsCurrentPasswordIsRejected() throws Exception {
        User member = createMember("pwd-same@belloisirs.test");
        String token = tokenFor(member.getEmail());

        mockMvc.perform(put(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(PASSWORD, PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    private String body(String currentPassword, String newPassword) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "currentPassword", currentPassword,
                "newPassword", newPassword));
    }

    private ResultActions login(String email, String password)
            throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password))));
    }
}