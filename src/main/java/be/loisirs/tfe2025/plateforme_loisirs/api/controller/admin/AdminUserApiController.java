package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import jakarta.validation.Valid;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AdminUserCreateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AdminUserUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.UserResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.UserMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {


    private static final String PARTNER_ROLE = "PARTNER";

    private final UserService userService;
    private final UserMapper userMapper;

    public AdminUserApiController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    // GET ALL USERS
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }


    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUser(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toResponseDTO(user));
    }


    // CREATE USER BY ADMIN
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody AdminUserCreateDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (isPartnerRole(dto.getRole())) {
            throw new IllegalArgumentException(
                    "Un partenaire se crée depuis la gestion des partenaires (compte et fiche ensemble).");
        }

        User user = userMapper.toEntity(dto);
        User savedUser = userService.addUser(user, dto.getRole());

        return ResponseEntity.ok(userMapper.toResponseDTO(savedUser));
    }


    // UPDATE USER BY ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateDTO dto
    ) {
        // Garder PARTNER pour un partenaire est permis (le front renvoie le rôle actuel
        // à chaque modification) ; seul le passage vers ou depuis PARTNER est refusé.
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            User existing = userService.getUser(id);

            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isPartner = existing.getRoles()
                    .stream()
                    .anyMatch(role -> PARTNER_ROLE.equals(role.getName()));

            if (isPartner != isPartnerRole(dto.getRole())) {
                throw new IllegalArgumentException(
                        "Le rôle partenaire ne peut être ni attribué ni retiré ici : il est lié à la fiche partenaire.");
            }
        }

        // a mettre dans one note ici transformes le DTO en objet User.
        User userToUpdate = userMapper.toEntity(dto);
        userToUpdate.setId(id);
        // a mettre dans one note
        // envoies cet objet au service, puis récupères le résultat modifié depuis la DB (via userservice)
        User updatedUser = userService.updateUser(userToUpdate, dto.getRole());

        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toResponseDTO(updatedUser));
    }


    // DELETE USER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        User existing = userService.getUser(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    private static boolean isPartnerRole(String role) {
        return role != null && PARTNER_ROLE.equalsIgnoreCase(role.trim());
    }
}