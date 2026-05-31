package be.loisirs.tfe2025.plateforme_loisirs.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageResponseDTO {
    private Long id;
    private String profileImageUrl;
}