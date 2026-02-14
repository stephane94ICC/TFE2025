package be.loisirs.tfe2025.plateforme_loisirs.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
