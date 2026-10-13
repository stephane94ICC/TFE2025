package be.loisirs.tfe2025.plateforme_loisirs.business;

import be.loisirs.tfe2025.plateforme_loisirs.common.AbstractIntegrationTest;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Vitrine publique des partenaires")
class PublicPartnerIntegrationTest extends AbstractIntegrationTest {

    private static final String SLUG = "bruxelles-yoga-studio";

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    @DisplayName("Un visiteur anonyme lit la vitrine : 200, sans aucun champ interne")
    void anonymousReadsShowcaseWithoutSensitiveFields() throws Exception {
        mockMvc.perform(get("/api/partners/{slug}", SLUG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(SLUG))
                .andExpect(jsonPath("$.name").isNotEmpty())
                // Garde-fou de regression : ces champs ne doivent jamais apparaitre
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.stripeAccountId").doesNotExist())
                .andExpect(jsonPath("$.commissionRate").doesNotExist())
                .andExpect(jsonPath("$.active").doesNotExist());
    }

    @Test
    @DisplayName("RGPD (minimisation) : seules les adresses CONTACT sont publiées, jamais LEGAL")
    void onlyContactAddressesArePublished() throws Exception {
        mockMvc.perform(get("/api/partners/{slug}", SLUG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addresses", not(empty())))
                .andExpect(jsonPath("$.addresses[*].type", everyItem(is("CONTACT"))));
    }

    @Test
    @DisplayName("Slug inconnu : 404")
    void unknownSlugIsNotFound() throws Exception {
        mockMvc.perform(get("/api/partners/{slug}", "inconnu"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Partenaire désactivé : 404, sa vitrine disparaît")
    void inactivePartnerIsNotFound() throws Exception {
        Partner partner = partnerRepository.findBySlugAndActiveTrue(SLUG)
                .orElseThrow(() -> new IllegalStateException(
                        "Partenaire de reference absent : migrations non rejouees."));

        partner.setActive(false);
        partnerRepository.saveAndFlush(partner);

        mockMvc.perform(get("/api/partners/{slug}", SLUG))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("La liste publique des activités porte le slug du partenaire")
    void publicActivitiesExposePartnerSlug() throws Exception {
        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].partnerSlug", hasItem(SLUG)));
    }

    @Test
    @DisplayName("Sécurité : ouvrir /api/partners/** n'ouvre pas l'espace partenaire /api/partner/**")
    void partnerSpaceStaysProtected() throws Exception {
        // Anonyme : 401, via le point d'entree HttpStatusEntryPoint de SecurityConfig
        mockMvc.perform(get("/api/partner/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Lien direct ou F5 sur /partners/{slug} : renvoyé vers l'application Vue")
    void showcasePageIsForwardedToSpa() throws Exception {
        mockMvc.perform(get("/partners/{slug}", SLUG))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }
}