package be.loisirs.tfe2025.plateforme_loisirs.api.controller;

import be.loisirs.tfe2025.plateforme_loisirs.dto.UserDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.UserMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    // ========================
    // GET ALL USERS
    // ========================
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ========================
    // GET USER BY ID
    // ========================
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // ========================
    // CREATE USER
    // ========================
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {

        // Vérification RGPD
        if (dto.getConsentRgpd() == null || !dto.getConsentRgpd()) {
            return ResponseEntity.badRequest().body(null); // message : "Vous devez accepter la RGPD"
        }

        User user = userMapper.toEntity(dto);

        // Vérification mot de passe et hash
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(userService.encodePassword(dto.getPassword()));
        } else {
            return ResponseEntity.badRequest().build(); // mot de passe obligatoire
        }

        userService.addUser(user);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    // ========================
    // UPDATE USER
    // ========================
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        User existing = userService.getUser(id);
        if (existing == null) return ResponseEntity.notFound().build();

        // Mise à jour via le mapper pour centraliser la logique
        userMapper.updateEntity(dto, existing);

        // Hash du mot de passe si modifié
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(userService.encodePassword(dto.getPassword()));
        }

        userService.updateUser(existing);
        return ResponseEntity.ok(userMapper.toDTO(existing));
    }

    // ========================
    // DELETE USER
    // ========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User existing = userService.getUser(id);
        if (existing == null) return ResponseEntity.notFound().build();

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}