package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.AuthResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.LoginRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.RegisterRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cette adresse e-mail.");
        }

        User user = new User();
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(registerRequestDTO.getPassword());
        user.setConsentRgpd(false);

        User savedUser = userRepository.save(user);

        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                "Inscription réussie."
        );
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Adresse e-mail ou mot de passe incorrect."));

        if (!user.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new RuntimeException("Adresse e-mail ou mot de passe incorrect.");
        }

        return new AuthResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                "Connexion réussie."
        );
    }
}