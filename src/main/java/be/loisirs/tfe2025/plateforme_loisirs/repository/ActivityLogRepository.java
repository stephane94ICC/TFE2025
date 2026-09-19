package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * Recherche filtrée et paginée.
     *
     * Chaque critère est ignoré lorsqu'il vaut null : un seul appel
     * couvre donc toutes les combinaisons de filtres, sans avoir à
     * multiplier les méthodes.
     */
    @Query("""
            SELECT a FROM ActivityLog a
            WHERE (:eventType IS NULL OR a.eventType = :eventType)
              AND (:email     IS NULL OR LOWER(a.userEmail) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:from      IS NULL OR a.createdAt >= :from)
              AND (:to        IS NULL OR a.createdAt <= :to)
            """)
    Page<ActivityLog> search(@Param("eventType") ActivityEventType eventType,
                             @Param("email") String email,
                             @Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to,
                             Pageable pageable);

    /**
     * Supprime en une seule requête SQL les entrées antérieures à la limite.
     *
     * Requête de masse volontaire : la méthode dérivée deleteByCreatedAtBefore
     * chargerait chaque entité en mémoire pour la supprimer une par une.
     * Le filtre porte sur created_at, indexé depuis V11_1.
     *
     * @return le nombre de lignes supprimées
     */
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.createdAt < :limit")
    int deleteOlderThan(@Param("limit") LocalDateTime limit);
}