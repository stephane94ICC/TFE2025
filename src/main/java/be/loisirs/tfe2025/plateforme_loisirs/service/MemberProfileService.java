package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AccountDeletionRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.InvalidCredentialsException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberProfileService {

    private static final String MEMBER_ROLE = "MEMBER";
    private static final String ANONYMIZED_FIRST_NAME = "Membre";
    private static final String ANONYMIZED_LAST_NAME = "supprimé";
    private static final String DEFAULT_PROFILE_IMAGE = "/uploads/members/default-profile.png";

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ActivityLogService activityLogService;
    private final ImageStorageService imageStorageService;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberProfileService(UserRepository userRepository,
                                ReservationRepository reservationRepository,
                                ActivityLogService activityLogService,
                                ImageStorageService imageStorageService,
                                BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.activityLogService = activityLogService;
        this.imageStorageService = imageStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public MemberProfileResponseDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        return toDTO(user);
    }

    @Transactional
    public MemberProfileResponseDTO updateProfile(String email, MemberProfileUpdateDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        String newPhone = normalizePhone(request.getPhone());

        List<String> changedFields = new ArrayList<>();

        if (!Objects.equals(user.getFirstName(), request.getFirstName())) {
            changedFields.add("prénom");
        }

        if (!Objects.equals(user.getLastName(), request.getLastName())) {
            changedFields.add("nom");
        }

        if (!Objects.equals(user.getPhone(), newPhone)) {
            changedFields.add("téléphone");
        }

        if (changedFields.isEmpty()) {
            return toDTO(user);
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(newPhone);

        User savedUser = userRepository.save(user);

        activityLogService.log(
                ActivityEventType.PROFILE_UPDATED,
                "User",
                savedUser.getId(),
                "Champs modifiés : " + String.join(", ", changedFields)
        );

        return toDTO(savedUser);
    }

    @Transactional
    public void deleteAccount(String email, AccountDeletionRequestDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Mot de passe incorrect.");
        }

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (roleNames.size() != 1 || !roleNames.contains(MEMBER_ROLE)) {
            throw new IllegalArgumentException(
                    "La suppression en libre-service est réservée aux comptes membres. "
                            + "Contactez l'administrateur.");
        }

        long upcomingReservations = reservationRepository.countUpcomingByUserEmailAndStatus(
                email, ReservationStatus.CONFIRMED, LocalDateTime.now());

        if (upcomingReservations > 0) {
            throw new IllegalArgumentException(
                    "Vous avez " + upcomingReservations + " réservation(s) à venir. "
                            + "Annulez-les avant de supprimer votre compte.");
        }

        Long userId = user.getId();
        String originalEmail = user.getEmail();
        String originalImageUrl = user.getProfileImageUrl();

        activityLogService.logForEmail(
                ActivityEventType.ACCOUNT_DELETION_REQUESTED,
                userId,
                originalEmail,
                "Demande de suppression confirmée par mot de passe"
        );

        user.setFirstName(ANONYMIZED_FIRST_NAME);
        user.setLastName(ANONYMIZED_LAST_NAME);
        user.setEmail("anonyme-" + userId + "@belloisirs.invalid");
        user.setPhone(null);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setProfileImageUrl(DEFAULT_PROFILE_IMAGE);
        user.setActive(false);

        userRepository.save(user);

        if (originalImageUrl != null && !DEFAULT_PROFILE_IMAGE.equals(originalImageUrl)) {
            try {
                imageStorageService.deleteImage(originalImageUrl);
            } catch (Exception exception) {
                // La suppression du fichier ne doit jamais annuler l'anonymisation.
            }
        }

        activityLogService.logForEmail(
                ActivityEventType.ACCOUNT_ANONYMIZED,
                userId,
                originalEmail,
                "Compte anonymisé et désactivé. "
                        + "Identités de facturation conservées (RGPD art. 17(3)(b))"
        );
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.trim();
    }

    private MemberProfileResponseDTO toDTO(User user) {
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .sorted()
                .toList();

        return new MemberProfileResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getProfileImageUrl(),
                roles
        );
    }
}