package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AdminUserCreateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AdminUserUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.UserResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // ========================
    // User -> Response DTO
    // ========================
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setConsentRgpd(user.getConsentRgpd());

        // Le mot de passe n'est jamais renvoyé au frontend
        return dto;
    }

    // ========================
    // AdminUserCreateDTO -> User
    // ========================
    public User toEntity(AdminUserCreateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(dto.getPassword());

        /*
         * L'admin ne peut pas accepter le RGPD à la place de l'utilisateur.
         * Donc, si un utilisateur est créé par l'admin, on met false par défaut.
         */
        user.setConsentRgpd(false);

        return user;
    }

    // ========================
    // AdminUserUpdateDTO -> User
    // ========================
    public User toEntity(AdminUserUpdateDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
        }

        return user;
    }
}