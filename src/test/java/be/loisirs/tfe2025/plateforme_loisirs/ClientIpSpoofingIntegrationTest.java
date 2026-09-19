package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLog;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Intégrité de l'adresse IP enregistrée dans le journal d'audit.
 *
 * L'en-tête X-Forwarded-For est envoyé par le client : il peut contenir
 * n'importe quelle valeur. Le journal ne doit enregistrer que l'adresse
 * déterminée par le serveur, sinon un attaquant pourrait falsifier la
 * trace de ses propres connexions.
 */
@DisplayName("Adresse IP du journal d'audit")
class ClientIpSpoofingIntegrationTest extends AbstractIntegrationTest {

    /** Adresse de documentation (RFC 5737) : ne peut appartenir à personne. */
    private static final String SPOOFED_IP = "203.0.113.66";

    /** Adresse du client simulé par MockMvc. */
    private static final String REAL_IP = "127.0.0.1";

    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("Un X-Forwarded-For envoyé par le client est ignoré")
    void spoofedForwardedHeaderIsIgnored() throws Exception {
        // E-mail unique : le journal (REQUIRES_NEW) survit au rollback du test,
        // les entrées des exécutions précédentes ne doivent pas interférer.
        String email = "ip-" + UUID.randomUUID().toString().substring(0, 8) + "@belloisirs.test";
        createMember(email);

        String body = "{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}";

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", SPOOFED_IP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        // Lecture dans une NOUVELLE transaction. Le journal est écrit en
        // REQUIRES_NEW, donc validé à part ; mais la transaction du test a
        // déjà lu la base (createMember) et MySQL, en REPEATABLE READ, lui
        // montre la base telle qu'elle était à cette première lecture.
        // Relue dans la transaction du test, l'entrée serait invisible.
        TransactionTemplate freshRead = new TransactionTemplate(transactionManager);
        freshRead.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        freshRead.setReadOnly(true);

        List<ActivityLog> entries = freshRead.execute(tx -> activityLogRepository
                .search(ActivityEventType.LOGIN_SUCCESS, email, null, null, PageRequest.of(0, 10))
                .getContent());

        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getIpAddress())
                .isEqualTo(REAL_IP)
                .isNotEqualTo(SPOOFED_IP);
    }
}