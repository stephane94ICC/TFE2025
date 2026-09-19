package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLog;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLogRepository;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityLogRetentionService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Rétention du journal d'audit (politique de confidentialité, section 5).
 *
 * La politique annonce 12 mois : ce test vérifie que le code applique
 * réellement cette durée, et pas seulement qu'elle est écrite.
 *
 * Transactionnel (hérité) : les entrées créées et la purge elle-même
 * sont annulées à la fin de chaque test.
 */
@DisplayName("Rétention du journal d'audit")
class ActivityLogRetentionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ActivityLogRetentionService retentionService;
    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private EntityManager entityManager;

    /**
     * Limite tronquée à la seconde : la colonne created_at est un DATETIME
     * sans fraction. Des nanosecondes feraient arrondir la valeur stockée
     * d'un côté ou de l'autre de la limite, et le test deviendrait aléatoire.
     */
    private LocalDateTime limit() {
        return LocalDateTime.now().minusMonths(12).truncatedTo(ChronoUnit.SECONDS);
    }

    /** Entrée datée explicitement : @PrePersist ne l'écrase pas si déjà remplie. */
    private ActivityLog entryAt(LocalDateTime createdAt) {
        ActivityLog entry = new ActivityLog();
        entry.setEventType(ActivityEventType.LOGIN_SUCCESS);
        entry.setUserEmail("retention@belloisirs.test");
        entry.setCreatedAt(createdAt);
        return activityLogRepository.saveAndFlush(entry);
    }

    @Test
    @DisplayName("Une entrée de plus de 12 mois est supprimée, une plus récente est conservée")
    void purgeDeletesExpiredAndKeepsRecent() {
        LocalDateTime limit = limit();
        ActivityLog expired = entryAt(limit.minusMonths(1));   // 13 mois
        ActivityLog recent = entryAt(limit.plusMonths(1));     // 11 mois

        int deleted = retentionService.purgeOlderThan(limit);

        // Le DELETE de masse passe directement en SQL, sans mettre à jour
        // le cache de Hibernate : sans clear(), findById renverrait encore
        // l'entrée supprimée depuis la mémoire, et le test mentirait.
        entityManager.clear();

        assertThat(deleted).isGreaterThanOrEqualTo(1);
        assertThat(activityLogRepository.findById(expired.getId())).isEmpty();
        assertThat(activityLogRepository.findById(recent.getId())).isPresent();
    }

    @Test
    @DisplayName("Une entrée exactement à la limite est conservée (comparaison stricte)")
    void entryExactlyAtLimitIsKept() {
        LocalDateTime limit = limit();
        ActivityLog boundary = entryAt(limit);

        retentionService.purgeOlderThan(limit);
        entityManager.clear();

        assertThat(activityLogRepository.findById(boundary.getId())).isPresent();
    }

    @Test
    @DisplayName("Une durée de rétention nulle est refusée au démarrage")
    void zeroRetentionIsRejected() {
        assertThatThrownBy(() -> new ActivityLogRetentionService(activityLogRepository, 0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("au moins 1");
    }
}