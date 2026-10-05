package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Révocation des jetons après changement de mot de passe")
class TokenRevocationIntegrationTest extends AbstractIntegrationTest {

    private static final String PROFILE_URL = "/api/member/profile";
    private static final String NEW_PASSWORD = "NouveauMotDePasse2?";

    private void waitForNextSecond() throws InterruptedException {
        Thread.sleep(1_100);
    }

    @Test
    @DisplayName("Après son propre changement : l'ancien jeton est refusé, le nouveau fonctionne")
    void ownPasswordChangeRevokesOldToken() throws Exception {
        User member = createMember("revoke-own@belloisirs.test");
        String oldToken = tokenFor(member.getEmail());

        // 1. L'ancien jeton fonctionne (sinon le 403 final ne prouverait rien).
        mockMvc.perform(get(PROFILE_URL).header(HttpHeaders.AUTHORIZATION, bearer(oldToken)))
                .andExpect(status().isOk());

        waitForNextSecond();

        // 2. Changement de mot de passe, fait avec ce même jeton.
        mockMvc.perform(put(PROFILE_URL + "/password")
                        .header(HttpHeaders.AUTHORIZATION, bearer(oldToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "currentPassword", PASSWORD,
                                "newPassword", NEW_PASSWORD))))
                .andExpect(status().isNoContent());

        // 3. Le MÊME jeton est désormais refusé (signature et expiration pourtant valides).
        mockMvc.perform(get(PROFILE_URL).header(HttpHeaders.AUTHORIZATION, bearer(oldToken)))
                .andExpect(status().isForbidden());

        // 4. Un jeton obtenu avec le nouveau mot de passe fonctionne.
        String newToken = objectMapper.readTree(mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                        "email", member.getEmail(),
                                        "password", NEW_PASSWORD))))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get(PROFILE_URL).header(HttpHeaders.AUTHORIZATION, bearer(newToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Mot de passe changé par l'admin : les sessions de l'utilisateur tombent")
    void adminPasswordResetRevokesUserTokens() throws Exception {
        User member = createMember("revoke-by-admin@belloisirs.test");
        String memberToken = tokenFor(member.getEmail());

        User admin = createMember("revoke-admin@belloisirs.test");
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rôle ADMIN absent : migrations non rejouées."));
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);
        String adminToken = tokenFor(admin.getEmail());

        waitForNextSecond();

        mockMvc.perform(put("/api/admin/users/{id}", member.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", NEW_PASSWORD))))
                .andExpect(status().isOk());

        mockMvc.perform(get(PROFILE_URL).header(HttpHeaders.AUTHORIZATION, bearer(memberToken)))
                .andExpect(status().isForbidden());
    }
}