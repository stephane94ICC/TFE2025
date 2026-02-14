package be.loisirs.tfe2025.plateforme_loisirs.api.controller;

import be.loisirs.tfe2025.plateforme_loisirs.dto.UserDTO;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.UserMapper;
import be.loisirs.tfe2025.plateforme_loisirs.model.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
        User user = userMapper.toEntity(dto);
        userService.addUser(user);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        User existing = userService.getUser(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setEmail(dto.getEmail());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setConsentRgpd(dto.getConsentRgpd());

        userService.updateUser(existing);
        return ResponseEntity.ok(userMapper.toDTO(existing));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User existing = userService.getUser(id);
        if (existing == null) return ResponseEntity.notFound().build();

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
