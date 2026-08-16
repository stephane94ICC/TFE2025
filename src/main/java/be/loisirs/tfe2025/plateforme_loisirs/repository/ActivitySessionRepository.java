package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivitySessionRepository extends JpaRepository<ActivitySession, Long> {

    List<ActivitySession> findAllByActivity_IdAndActivity_Partner_User_Email(Long activityId, String email);

    Optional<ActivitySession> findByIdAndActivity_Partner_User_Email(Long id, String email);

    List<ActivitySession> findAllByActivity_IdAndStatus(Long activityId, ActivitySessionStatus status);

    Optional<ActivitySession> findByIdAndStatus(Long id, ActivitySessionStatus status);
}