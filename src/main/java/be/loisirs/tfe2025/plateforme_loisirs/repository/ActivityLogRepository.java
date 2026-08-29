package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByEventType(ActivityEventType eventType, Pageable pageable);

    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);

    Page<ActivityLog> findByUserEmailContainingIgnoreCase(String userEmail, Pageable pageable);

    Page<ActivityLog> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}