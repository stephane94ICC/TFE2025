package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByUserId(Long userId);

    Optional<Partner> findByUserEmail(String email);

    boolean existsBySlug(String slug);

    boolean existsByEnterpriseNumber(String enterpriseNumber);

    Optional<Partner> findBySlugAndActiveTrue(String slug);

    /**
     * Lecture verrouillée (SELECT ... FOR UPDATE) : file d'attente pour l'attribution
     * des numéros de justificatif d'un partenaire. Le verrou dure jusqu'à la fin de la
     * transaction appelante ; un second téléchargement simultané attend son tour.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Partner p WHERE p.id = :id")
    Optional<Partner> findWithLockById(@Param("id") Long id);
}