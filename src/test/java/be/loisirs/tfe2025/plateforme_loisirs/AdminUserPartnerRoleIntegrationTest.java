package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Le role PARTNER ne se gere pas par /api/admin/users : il est lie a une fiche.
 * Sinon : compte PARTNER sans fiche (promotion) ou fiche sans acces (retrait).
 *
 * Le dernier test protege contre l'exces inverse : le front renvoie le role
 * actuel a chaque modification, un refus naif de "PARTNER" empecherait
 * simplement de renommer un partenaire.
 */
@DisplayName("Administration des utilisateurs — rôle PARTNER réservé à la création de partenaire")
class AdminUserPartnerRoleIntegrationTest extends AbstractIntegrationTest {

    private static final String URL = "/api/admin/users";

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private EntityManager entityManager;

    private String adminToken;

    @BeforeEach
    void loginAsAdmin() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "Role ADMIN absent : les migrations n'ont pas ete rejouees."));

        User admin = createMember("role-guard-admin@belloisirs.test");
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        adminToken = tokenFor(admin.getEmail());
    }

    @Test
    @DisplayName("Créer un utilisateur PARTNER ici : 400, aucun compte")
    void createWithPartnerRoleIsRejected() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "email", "partner-sans-fiche@belloisirs.test",
                "firstName", "Prenom",
                "lastName", "Test",
                "role", "PARTNER",
                "password", PASSWORD));

        mockMvc.perform(post(URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.existsByEmail("partner-sans-fiche@belloisirs.test")).isFalse();
    }

    @Test
    @DisplayName("Promouvoir un membre en PARTNER : 400, rôle inchangé")
    void promoteMemberToPartnerIsRejected() throws Exception {
        User member = createMember("role-guard-member@belloisirs.test");

        mockMvc.perform(put(URL + "/{id}", member.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("role", "PARTNER"))))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.findById(member.getId()).orElseThrow().getRoles())
                .extracting("name").containsExactly("MEMBER");
    }

    @Test
    @DisplayName("Retirer le rôle PARTNER : 400, le partenaire garde l'accès à sa fiche")
    void demotePartnerIsRejected() throws Exception {
        Long partnerUserId = anyPartnerUserId();

        mockMvc.perform(put(URL + "/{id}", partnerUserId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("role", "MEMBER"))))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.findById(partnerUserId).orElseThrow().getRoles())
                .extracting("name").contains("PARTNER");
    }

    @Test
    @DisplayName("Modifier un partenaire en gardant son rôle : 200")
    void editPartnerKeepingRoleIsAllowed() throws Exception {
        Long partnerUserId = anyPartnerUserId();

        mockMvc.perform(put(URL + "/{id}", partnerUserId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "firstName", "Renomme",
                                "role", "PARTNER"))))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.findById(partnerUserId).orElseThrow().getFirstName())
                .isEqualTo("Renomme");
    }

    private Long anyPartnerUserId() {
        Partner partner = partnerRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun partenaire dans le jeu de donnees : migrations non rejouees."));
        return partner.getUser().getId();
    }
}