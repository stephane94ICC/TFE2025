package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Administration des partenaires : liste et taux de commission.
 *
 * La creation du compte Stripe n'est PAS testee ici : elle exige un appel
 * reseau vers Stripe, et les tests n'en font aucun (cle vide dans le profil
 * test). Elle est verifiee manuellement depuis l'ecran d'administration.
 *
 * La liste, elle, ne touche pas Stripe : aucun partenaire du jeu de donnees
 * n'a de compte, l'etat NOT_CREATED est donc calcule sans appel reseau.
 */
@DisplayName("Administration des partenaires — commission")
class AdminPartnerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("L'admin voit tous les partenaires, à 5 % et sans compte de paiement")
    void adminListsPartners() throws Exception {
        String token = tokenFor(createAdmin("partners-list@belloisirs.test").getEmail());

        mockMvc.perform(get("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) partnerRepository.count()))
                .andExpect(jsonPath("$[0].commissionRate").value(5.0))
                .andExpect(jsonPath("$[0].paymentStatus").value("NOT_CREATED"));
    }

    @Test
    @DisplayName("Un taux dans la fourchette est enregistré : 200")
    void validRateIsSaved() throws Exception {
        Partner partner = anyPartner();
        String token = tokenFor(createAdmin("partners-rate-ok@belloisirs.test").getEmail());

        mockMvc.perform(put("/api/admin/partners/{id}/commission-rate", partner.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commissionRate\": 6.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commissionRate").value(6.5));

        // Relire la base, pas le cache Hibernate
        entityManager.flush();
        entityManager.clear();
        assertThat(partnerRepository.findById(partner.getId()).orElseThrow().getCommissionRate())
                .isEqualByComparingTo("6.50");
    }

    @Test
    @DisplayName("Un taux hors fourchette est refusé par le serveur : 400, base inchangée")
    void outOfRangeRateIsRejected() throws Exception {
        Partner partner = anyPartner();
        BigDecimal before = partner.getCommissionRate();
        String token = tokenFor(createAdmin("partners-rate-ko@belloisirs.test").getEmail());

        mockMvc.perform(put("/api/admin/partners/{id}/commission-rate", partner.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commissionRate\": 9}"))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();
        assertThat(partnerRepository.findById(partner.getId()).orElseThrow().getCommissionRate())
                .isEqualByComparingTo(before);
    }

    @Test
    @DisplayName("Partenaire inexistant : 404")
    void unknownPartnerIsNotFound() throws Exception {
        String token = tokenFor(createAdmin("partners-404@belloisirs.test").getEmail());

        mockMvc.perform(put("/api/admin/partners/{id}/commission-rate", 999_999L)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commissionRate\": 5}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Un membre n'accède pas à l'administration des partenaires : 403")
    void memberIsForbidden() throws Exception {
        String token = tokenFor(createMember("partners-member@belloisirs.test").getEmail());

        mockMvc.perform(get("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isForbidden());
    }

    private User createAdmin(String email) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "Role ADMIN absent : les migrations n'ont pas ete rejouees."));

        User admin = createMember(email);
        admin.getRoles().add(adminRole);
        return userRepository.save(admin);
    }

    private Partner anyPartner() {
        return partnerRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun partenaire dans le jeu de donnees : migrations non rejouees."));
    }
}