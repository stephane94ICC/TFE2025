package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByStripeSessionId(String stripeSessionId);

    List<Reservation> findAllByUser_Email(String email);

    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM Reservation r " +
            "WHERE r.session.id = :sessionId AND r.status IN :statuses")
    int sumQuantityBySessionIdAndStatusIn(
            @Param("sessionId") Long sessionId,
            @Param("statuses") List<ReservationStatus> statuses
    );
}