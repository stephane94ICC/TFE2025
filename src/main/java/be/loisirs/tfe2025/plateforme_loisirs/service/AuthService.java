package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.EmailAlreadyUsedException;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.InvalidCredentialsException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.AuthResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.LoginRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.RegisterRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.repository.RoleRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ActivityLogService activityLogService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService,
            ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.activityLogService = activityLogService;
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new EmailAlreadyUsedException("Un compte existe déjà avec cette adresse e-mail.");
        }
        if (!Boolean.TRUE.equals(registerRequestDTO.getConsentRgpd())) {
            throw new IllegalArgumentException("Le consentement RGPD est obligatoire.");
        }

        Role memberRole = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Le rôle MEMBER est introuvable."));

        User user = new User();
        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setEmail(registerRequestDTO.getEmail());

        String hashedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());
        user.setPassword(hashedPassword);

        user.setConsentRgpd(true);

        user.setRoles(new HashSet<>());
        user.getRoles().add(memberRole);

        User savedUser = userRepository.save(user);

        activityLogService.logForEmail(
                ActivityEventType.REGISTER, savedUser.getId(), savedUser.getEmail(), null);

        return buildAuthResponse(savedUser, "Inscription réussie.");
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String email = loginRequestDTO.getEmail();

        User user = userRepository.findByEmail(email).orElse(null);


        if (user == null) {
            activityLogService.logForEmail(
                    ActivityEventType.LOGIN_FAILURE, null, email, "Adresse e-mail inconnue");
            throw new InvalidCredentialsException("Adresse e-mail ou mot de passe incorrect.");
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            activityLogService.logForEmail(
                    ActivityEventType.LOGIN_FAILURE, user.getId(), email, "Mot de passe incorrect");
            throw new InvalidCredentialsException("Adresse e-mail ou mot de passe incorrect.");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            activityLogService.logForEmail(
                    ActivityEventType.ACCESS_DENIED_INACTIVE_ACCOUNT, user.getId(), email,
                    "Tentative de connexion sur un compte désactivé");
            throw new InvalidCredentialsException("Ce compte n'est plus actif.");
        }

        activityLogService.logForEmail(
                ActivityEventType.LOGIN_SUCCESS, user.getId(), email, null);

        return buildAuthResponse(user, "Connexion réussie.");
    }

    private AuthResponseDTO buildAuthResponse(User user, String message) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roles,
                user.getProfileImageUrl(),
                token,
                message
        );
    }
}