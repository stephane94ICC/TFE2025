package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Activity;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @EntityGraph(attributePaths = {"partner", "images", "reviewedByUser"})
    List<Activity> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"partner", "images", "reviewedByUser"})
    Optional<Activity> findOneById(Long id);

    @EntityGraph(attributePaths = {"partner", "images"})
    List<Activity> findAllByStatusOrderByCreatedAtDesc(ActivityStatus status);

    @EntityGraph(attributePaths = {"partner", "images"})
    Optional<Activity> findByIdAndStatus(Long id, ActivityStatus status);

    @EntityGraph(attributePaths = {"partner", "images", "reviewedByUser"})
    List<Activity> findAllByPartner_User_EmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = {"partner", "images", "reviewedByUser"})
    Optional<Activity> findByIdAndPartner_User_Email(Long id, String email);
}
