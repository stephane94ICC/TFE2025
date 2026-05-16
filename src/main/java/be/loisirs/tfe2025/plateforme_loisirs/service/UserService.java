package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.RoleRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addUser(User user, String roleName) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(encodePassword(user.getPassword()));
        }

        if (user.getConsentRgpd() == null) {
            user.setConsentRgpd(false);
        }

        String finalRoleName = (roleName == null || roleName.isBlank()) ? "MEMBER" : roleName;

        Role role = roleRepository.findByName(finalRoleName)
                .orElseThrow(() -> new RuntimeException("Le rôle " + finalRoleName + " est introuvable."));

        user.setRoles(new HashSet<>());
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    public User updateUser(User user, String roleName) {
        User existing = userRepository.findById(user.getId()).orElse(null);

        if (existing == null) {
            return null;
        }

        if (user.getEmail() != null) {
            existing.setEmail(user.getEmail());
        }

        if (user.getFirstName() != null) {
            existing.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null) {
            existing.setLastName(user.getLastName());
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(encodePassword(user.getPassword()));
        }

        if (roleName != null && !roleName.isBlank()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Le rôle " + roleName + " est introuvable."));

            existing.getRoles().clear();
            existing.getRoles().add(role);
        }

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}