package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.EnterpriseNumberAlreadyUsedException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Administration des partenaires : liste, taux de commission et creation.
 *
 * La creation du compte Stripe n'est PAS testee ici : elle exige un appel
 * reseau vers Stripe, et les tests n'en font aucun (cle vide dans le profil
 * test). Elle est verifiee manuellement depuis l'ecran d'administration.
 *
 * La liste, elle, ne touche pas Stripe : aucun partenaire du jeu de donnees
 * n'a de compte, l'etat NOT_CREATED est donc calcule sans appel reseau.
 */
@DisplayName("Administration des partenaires — commission et création")
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

    // ------------------------------------------------------------------
    // Creation d'un partenaire (compte + fiche)
    // ------------------------------------------------------------------

    /** N° d'entreprise valide (01234567 mod 97 = 48, cle 49), saisi avec des points. */
    private static final String VALID_ENTERPRISE_NUMBER = "0123.456.749";

    @Test
    @DisplayName("Création : 201, compte PARTNER + fiche, TVA déduite, valeurs par défaut")
    void adminCreatesPartner() throws Exception {
        String token = tokenFor(createAdmin("partners-create@belloisirs.test").getEmail());
        String email = "nouveau-partenaire@belloisirs.test";

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("Nouveau-Partenaire@Belloisirs.test", VALID_ENTERPRISE_NUMBER)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Atelier Test"))
                .andExpect(jsonPath("$.vatNumber").value("BE0123456749"))
                .andExpect(jsonPath("$.commissionRate").value(5.0))
                .andExpect(jsonPath("$.paymentStatus").value("NOT_CREATED"));

        // Relire MySQL, pas le cache Hibernate
        entityManager.flush();
        entityManager.clear();

        User user = userRepository.findByEmail(email).orElseThrow();
        assertThat(user.getEmail()).isEqualTo(email); // mis en minuscules
        assertThat(user.getRoles()).extracting("name").containsExactly("PARTNER");
        assertThat(user.getConsentRgpd()).isFalse(); // base legale : le contrat
        assertThat(passwordEncoder.matches(PASSWORD, user.getPassword())).isTrue(); // hache, pas en clair

        Partner partner = partnerRepository.findByUserEmail(email).orElseThrow();
        assertThat(partner.getEnterpriseNumber()).isEqualTo("0123456749"); // normalise
        assertThat(partner.getVatNumber()).isEqualTo("BE0123456749");
        assertThat(partner.getSlug()).startsWith("atelier-test-");
        assertThat(partner.getLogoUrl()).isEqualTo("/uploads/partners/default-logo.png");
        assertThat(partner.getCommissionRate()).isEqualByComparingTo("5.00");
    }

    @Test
    @DisplayName("Le partenaire créé se connecte et accède à son espace : 200")
    void createdPartnerCanUseHisSpace() throws Exception {
        String adminToken = tokenFor(createAdmin("partners-login@belloisirs.test").getEmail());

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("partenaire-connexion@belloisirs.test", VALID_ENTERPRISE_NUMBER)))
                .andExpect(status().isCreated());

        // Vrai jeton via /api/auth/login, puis route reservee au role PARTNER
        String partnerToken = tokenFor("partenaire-connexion@belloisirs.test");

        mockMvc.perform(get("/api/partner/profile")
                        .header(HttpHeaders.AUTHORIZATION, bearer(partnerToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("N° d'entreprise déjà utilisé : 409 avec code, aucun second compte")
    void duplicateEnterpriseNumberIsRejected() throws Exception {
        String token = tokenFor(createAdmin("partners-dup-bce@belloisirs.test").getEmail());

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("premier@belloisirs.test", VALID_ENTERPRISE_NUMBER)))
                .andExpect(status().isCreated());

        // Meme numero, ecrit autrement : la normalisation doit le reconnaitre
        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("second@belloisirs.test", "0123 456 749")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(EnterpriseNumberAlreadyUsedException.CODE));

        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.existsByEmail("second@belloisirs.test")).isFalse();
    }

    @Test
    @DisplayName("E-mail déjà utilisé : 409 sans code, aucune fiche créée")
    void duplicateEmailIsRejected() throws Exception {
        String token = tokenFor(createAdmin("partners-dup-mail@belloisirs.test").getEmail());
        createMember("deja-membre@belloisirs.test");
        long partnersBefore = partnerRepository.count();

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("deja-membre@belloisirs.test", VALID_ENTERPRISE_NUMBER)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").doesNotExist());

        entityManager.flush();
        entityManager.clear();
        assertThat(partnerRepository.count()).isEqualTo(partnersBefore);
    }

    @Test
    @DisplayName("Clé modulo 97 fausse : 400, rien n'est créé")
    void invalidEnterpriseNumberIsRejected() throws Exception {
        String token = tokenFor(createAdmin("partners-mod97@belloisirs.test").getEmail());

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("cle-fausse@belloisirs.test", "0123.456.748")))
                .andExpect(status().isBadRequest());

        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.existsByEmail("cle-fausse@belloisirs.test")).isFalse();
    }

    @Test
    @DisplayName("Un membre ne crée pas de partenaire : 403")
    void memberCannotCreatePartner() throws Exception {
        String token = tokenFor(createMember("partners-create-member@belloisirs.test").getEmail());

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody("intrus@belloisirs.test", VALID_ENTERPRISE_NUMBER)))
                .andExpect(status().isForbidden());
    }

    /** Corps JSON complet ; le mot de passe respecte PasswordPolicy. */
    private String createBody(String email, String enterpriseNumber) {
        return """
                {
                  "email": "%s",
                  "firstName": "Prenom",
                  "lastName": "Partenaire",
                  "password": "%s",
                  "name": "Atelier Test",
                  "enterpriseNumber": "%s",
                  "phone": "+32 2 555 01 99",
                  "contactEmail": "contact@atelier-test.example",
                  "website": "https://atelier-test.example"
                }
                """.formatted(email, PASSWORD, enterpriseNumber);
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