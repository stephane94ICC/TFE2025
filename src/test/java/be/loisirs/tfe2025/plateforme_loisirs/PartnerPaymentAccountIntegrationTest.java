package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Compte de paiement vu par le partenaire.
 *
 * Seuls les cas SANS compte Stripe sont testes : ils ne font aucun appel
 * reseau (etat NOT_CREATED calcule localement, lien refuse avant tout
 * appel). Le parcours d'inscription Stripe est verifie manuellement.
 */
@DisplayName("Compte de paiement — côté partenaire")
class PartnerPaymentAccountIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    @DisplayName("Sans compte créé par l'admin : état NOT_CREATED")
    void statusWithoutAccountIsNotCreated() throws Exception {
        User partnerUser = createPartner("payment-status@belloisirs.test", "0999000001");
        String token = tokenFor(partnerUser.getEmail());

        mockMvc.perform(get("/api/partner/payment-account")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_CREATED"))
                .andExpect(jsonPath("$.onboardingUrl").doesNotExist());
    }

    @Test
    @DisplayName("Sans compte créé par l'admin : le lien d'inscription est refusé (400)")
    void onboardingLinkWithoutAccountIsRejected() throws Exception {
        User partnerUser = createPartner("payment-link@belloisirs.test", "0999000002");
        String token = tokenFor(partnerUser.getEmail());

        mockMvc.perform(post("/api/partner/payment-account/onboarding-link")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Un membre n'accède pas au compte de paiement partenaire : 403")
    void memberIsForbidden() throws Exception {
        String token = tokenFor(createMember("payment-member@belloisirs.test").getEmail());

        mockMvc.perform(get("/api/partner/payment-account")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isForbidden());
    }

    /**
     * Partenaire cree pour le test : ceux du jeu de donnees ont des
     * mots de passe inconnus. Numeros d'entreprise et de TVA uniques
     * (contraintes UNIQUE de la table partner).
     */
    private User createPartner(String email, String number) {
        Role partnerRole = roleRepository.findByName("PARTNER")
                .orElseThrow(() -> new IllegalStateException(
                        "Role PARTNER absent : les migrations n'ont pas ete rejouees."));

        User user = createMember(email);
        user.getRoles().add(partnerRole);
        userRepository.save(user);

        Partner partner = new Partner();
        partner.setUser(user);
        partner.setName("Partenaire de test " + number);
        partner.setEnterpriseNumber(number);
        partner.setVatNumber("BE" + number);
        partnerRepository.save(partner);

        return user;
    }
}