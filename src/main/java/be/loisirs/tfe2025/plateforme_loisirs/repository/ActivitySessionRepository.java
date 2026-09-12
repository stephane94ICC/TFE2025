package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivitySessionRepository extends JpaRepository<ActivitySession, Long> {

    List<ActivitySession> findAllByActivity_IdAndActivity_Partner_User_Email(
            Long activityId,
            String email
    );

    Optional<ActivitySession> findByIdAndActivity_Partner_User_Email(
            Long id,
            String email
    );

    List<ActivitySession> findAllByActivity_IdAndStatus(
            Long activityId,
            ActivitySessionStatus status
    );

    Optional<ActivitySession> findByIdAndStatus(
            Long id,
            ActivitySessionStatus status
    );

    List<ActivitySession> findAllByActivity_IdAndStatusAndStartAtAfterOrderByStartAtAsc(
            Long activityId,
            ActivitySessionStatus status,
            LocalDateTime startAt
    );

    @Query("""
            SELECT DISTINCT s.activity.id, s.location.city
            FROM ActivitySession s
            WHERE s.activity.id IN :activityIds
              AND s.location.city IS NOT NULL
              AND s.location.city <> ''
            ORDER BY s.activity.id, s.location.city
            """)
    List<Object[]> findDistinctCitiesByActivityIds(
            @Param("activityIds") List<Long> activityIds
    );

    @Query("""
            SELECT DISTINCT s.activity.id
            FROM ActivitySession s
            WHERE s.activity.id IN :activityIds
              AND s.status = :sessionStatus
              AND s.startAt > :now
              AND s.bookingDeadline > :now
              AND s.capacity > (
                  SELECT COALESCE(SUM(r.quantity), 0)
                  FROM Reservation r
                  WHERE r.session.id = s.id
                    AND r.status IN :reservationStatuses
              )
            """)
    List<Long> findActivityIdsWithAvailableSession(
            @Param("activityIds") List<Long> activityIds,
            @Param("sessionStatus") ActivitySessionStatus sessionStatus,
            @Param("reservationStatuses") List<ReservationStatus> reservationStatuses,
            @Param("now") LocalDateTime now
    );
}