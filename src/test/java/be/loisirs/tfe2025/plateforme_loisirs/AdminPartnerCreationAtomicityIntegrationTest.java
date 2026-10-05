package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLogRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Preuve que la creation d'un partenaire est atomique : si la fiche echoue
 * APRES la creation du compte, le compte est annule lui aussi.
 *
 * Pourquoi une classe a part, NON transactionnelle :
 * dans une classe @Transactional, la transaction du service REJOINT celle du
 * test (MockMvc tourne dans le meme fil). Un rollback du service ne ferait
 * alors que marquer la transaction du test, et le compte resterait visible :
 * le test ne prouverait rien. Ici, le service a sa vraie transaction,
 * validee ou annulee en base comme en production.
 *
 * Comment provoquer l'echec sans mock : un partenaire de demo recoit
 * temporairement la TVA BE0123456749. Le controle prealable porte sur le
 * n° d'entreprise et passe ; MySQL refuse ensuite l'insertion de la fiche
 * sur la contrainte UNIQUE de vat_number, alors que le compte est deja insere.
 *
 * Contrepartie : ce qui est ecrit est reellement valide en base,
 * d'ou le nettoyage avant et apres chaque test.
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("Création de partenaire — atomicité (transaction réelle)")
class AdminPartnerCreationAtomicityIntegrationTest extends AbstractIntegrationTest {

    private static final String ADMIN_EMAIL = "atomicite-admin@belloisirs.test";
    private static final String PARTNER_EMAIL = "atomicite-partenaire@belloisirs.test";
    private static final String CONFLICTING_VAT = "BE0123456749";

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private Long demoPartnerId;
    private String originalVat;

    @BeforeEach
    void prepareConflict() {
        cleanUp(); // restes eventuels d'une execution interrompue

        Partner demo = partnerRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun partenaire dans le jeu de donnees : migrations non rejouees."));

        demoPartnerId = demo.getId();
        originalVat = demo.getVatNumber();

        demo.setVatNumber(CONFLICTING_VAT);
        partnerRepository.save(demo);
    }

    @AfterEach
    void restore() {
        if (demoPartnerId != null) {
            partnerRepository.findById(demoPartnerId).ifPresent(partner -> {
                partner.setVatNumber(originalVat);
                partnerRepository.save(partner);
            });
        }
        cleanUp();
    }

    @Test
    @DisplayName("Fiche refusée par MySQL : 409, aucun compte orphelin, rien au journal")
    void failedPartnerLeavesNoOrphanAccount() throws Exception {
        String token = tokenFor(createAdmin().getEmail());
        long createdLogsBefore = countPartnerCreatedLogs();

        mockMvc.perform(post("/api/admin/partners")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody()))
                .andExpect(status().isConflict());

        // Pas de transaction de test : chaque lecture va reellement en base
        assertThat(userRepository.existsByEmail(PARTNER_EMAIL))
                .as("le compte doit etre annule avec la fiche")
                .isFalse();
        assertThat(countPartnerCreatedLogs())
                .as("aucune creation ne doit etre journalisee")
                .isEqualTo(createdLogsBefore);
    }

    private User createAdmin() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "Role ADMIN absent : les migrations n'ont pas ete rejouees."));

        User admin = createMember(ADMIN_EMAIL);
        admin.getRoles().add(adminRole);
        return userRepository.save(admin);
    }

    private long countPartnerCreatedLogs() {
        return activityLogRepository
                .search(ActivityEventType.PARTNER_CREATED, null, null, null, PageRequest.of(0, 1))
                .getTotalElements();
    }

    /** Supprime les comptes du test ; user_role suit (cote proprietaire du ManyToMany). */
    private void cleanUp() {
        userRepository.findByEmail(PARTNER_EMAIL).ifPresent(userRepository::delete);
        userRepository.findByEmail(ADMIN_EMAIL).ifPresent(userRepository::delete);
    }

    private String createBody() {
        return """
                {
                  "email": "%s",
                  "firstName": "Prenom",
                  "lastName": "Partenaire",
                  "password": "%s",
                  "name": "Atelier Atomicite",
                  "enterpriseNumber": "0123.456.749"
                }
                """.formatted(PARTNER_EMAIL, PASSWORD);
    }
}