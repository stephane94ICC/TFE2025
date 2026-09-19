package be.loisirs.tfe2025.plateforme_loisirs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Active l'exécution des méthodes annotées @Scheduled.
 *
 * Sans cette classe, @Scheduled est ignoré sans aucun avertissement :
 * la purge du journal d'audit ne s'exécuterait jamais.
 * Placée dans une classe dédiée plutôt que sur la classe principale,
 * pour que chaque responsabilité de configuration reste identifiable.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}