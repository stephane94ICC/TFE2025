package be.loisirs.tfe2025.plateforme_loisirs.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {
    private Long userId;
    private Long roleId;
}
