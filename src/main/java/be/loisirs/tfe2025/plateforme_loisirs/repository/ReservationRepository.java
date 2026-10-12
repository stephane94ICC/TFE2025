package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.DocumentType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByStripeSessionId(String stripeSessionId);
    Optional<Reservation> findByIdAndUser_Email(Long id, String email);
    List<Reservation> findAllByUser_EmailOrderByBookedAtDesc(String email);

    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM Reservation r " +
            "WHERE r.session.id = :sessionId AND r.status IN :statuses")
    int sumQuantityBySessionIdAndStatusIn(
            @Param("sessionId") Long sessionId,
            @Param("statuses") List<ReservationStatus> statuses
    );
        @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.user.email = :email " +
            "AND r.status = :status " +
            "AND r.session.startAt > :now")
    long countUpcomingByUserEmailAndStatus(
            @Param("email") String email,
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now
    );


    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.session s " +
            "JOIN FETCH s.activity a " +
            "WHERE a.partner.user.email = :email " +
            "AND (r.status = :confirmed " +
            "     OR (r.refundedAt IS NOT NULL AND EXISTS (" +
            "         SELECT d.id FROM PartnerDocument d " +
            "         WHERE d.reservation = r AND d.documentType = :invoice))) " +
            "ORDER BY r.bookedAt DESC, r.id DESC")
    List<Reservation> findPartnerSales(
            @Param("email") String email,
            @Param("confirmed") ReservationStatus confirmed,
            @Param("invoice") DocumentType invoice
    );


    Optional<Reservation> findByIdAndSession_Activity_Partner_User_Email(Long id, String email);
}