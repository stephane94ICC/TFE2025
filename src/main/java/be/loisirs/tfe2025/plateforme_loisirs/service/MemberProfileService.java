package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MemberProfileService {

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public MemberProfileService(UserRepository userRepository,
                                ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
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