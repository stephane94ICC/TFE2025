package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.UserDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // ========================
    // User -> DTO
    // ========================
    public UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setConsentRgpd(user.getConsentRgpd());
        // password jamais renvoyé au front
        return dto;
    }

    // ========================
    // DTO -> User (nouvelle entité)
    // ========================
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setConsentRgpd(dto.getConsentRgpd());

        // Mot de passe uniquement si fourni
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
        }

        return user;
    }

    // ========================
    // Mise à jour d'un User existant depuis DTO
    // ========================
    public void updateEntity(UserDTO dto, User existing) {
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) existing.setLastName(dto.getLastName());

        // RGPD : ne modifier que si fourni
        if (dto.getConsentRgpd() != null) existing.setConsentRgpd(dto.getConsentRgpd());

        // Password à mettre à jour seulement si fourni
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(dto.getPassword());
        }
    }
}