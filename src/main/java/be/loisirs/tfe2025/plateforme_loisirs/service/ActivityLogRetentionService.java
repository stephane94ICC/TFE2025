package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Rétention du journal d'audit.
 *
 * Applique la durée annoncée dans la politique de confidentialité
 * (section 5) : les entrées de plus de 12 mois sont supprimées.
 * Base légale du journal : intérêt légitime (sécurité), qui ne
 * justifie pas une conservation illimitée (RGPD, art. 5.1.e).
 */
@Service
@Transactional
public class ActivityLogRetentionService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogRetentionService.class);

    private final ActivityLogRepository activityLogRepository;
    private final int retentionMonths;

    public ActivityLogRetentionService(ActivityLogRepository activityLogRepository,
                                       @Value("${app.activity-log.retention-months:12}") int retentionMonths) {
        // Garde-fou : une valeur 0 ou négative effacerait tout le journal.
        if (retentionMonths < 1) {
            throw new IllegalStateException(
                    "app.activity-log.retention-months doit valoir au moins 1 (reçu : " + retentionMonths + ")");
        }
        this.activityLogRepository = activityLogRepository;
        this.retentionMonths = retentionMonths;
    }

    /**
     * Exécution automatique chaque nuit à 3 h, heure de Bruxelles.
     * Idempotente : une nuit manquée (serveur arrêté) est rattrapée
     * la nuit suivante, puisque le critère est « plus ancien que ».
     */
        @Scheduled(cron = "${app.activity-log.purge-cron:0 0 3 * * *}", zone = "Europe/Brussels")
    public void purgeExpired() {
        purgeOlderThan(LocalDateTime.now().minusMonths(retentionMonths));
    }

    /**
     * Supprime les entrées antérieures à la limite et trace le résultat.
     * Séparée de purgeExpired() pour être testable avec une date fixe.
     *
     * @return le nombre d'entrées supprimées
     */
    public int purgeOlderThan(LocalDateTime limit) {
        int deleted = activityLogRepository.deleteOlderThan(limit);
        logger.info("Rétention du journal d'audit : {} entrée(s) antérieure(s) au {} supprimée(s)",
                deleted, limit);
        return deleted;
    }
}