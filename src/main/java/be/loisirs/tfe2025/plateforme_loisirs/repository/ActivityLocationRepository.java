package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityLocationRepository extends JpaRepository<ActivityLocation, Long> {

    List<ActivityLocation> findAllByPartner_User_Email(String email);

    Optional<ActivityLocation> findByIdAndPartner_User_Email(Long id, String email);
}