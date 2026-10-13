package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Incrément atomique en base ; clear() pour qu'une relecture dans la même transaction voie la nouvelle valeur.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.sessionVersion = u.sessionVersion + 1 WHERE u.email = :email")
    int incrementSessionVersion(@Param("email") String email);
}