package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Règle de mot de passe dans l'administration des utilisateurs")
class AdminUserPasswordPolicyIntegrationTest extends AbstractIntegrationTest {

    private static final String URL = "/api/admin/users";
    private static final String STRONG_PASSWORD = "MotDePasseAdmin3#";

    private String adminToken;

    @BeforeEach
    void loginAsAdmin() throws Exception {
        User admin = createMember("policy-admin@belloisirs.test");
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rôle ADMIN absent : migrations non rejouées."));
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);
        adminToken = tokenFor(admin.getEmail());
    }

    private String createBody(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "email", email,
                "firstName", "Prenom",
                "lastName", "Test",
                "role", "MEMBER",
                "password", password));
    }

    @Test
    @DisplayName("Création avec un mot de passe faible (« 123 ») : 400")
    void createWithWeakPasswordIsRejected() throws Exception {
        mockMvc.perform(post(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("policy-weak@belloisirs.test", "123")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Création avec un mot de passe conforme : 200")
    void createWithStrongPasswordIsAccepted() throws Exception {
        mockMvc.perform(post(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("policy-strong@belloisirs.test", STRONG_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Modification : mot de passe faible refusé (400), sans mot de passe accepté (inchangé)")
    void updateAppliesPolicyOnlyWhenPasswordIsGiven() throws Exception {
        User member = createMember("policy-update@belloisirs.test");

        mockMvc.perform(put(URL + "/{id}", member.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("password", "123"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put(URL + "/{id}", member.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("firstName", "Renomme"))))
                .andExpect(status().isOk());
    }
}