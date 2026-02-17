package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ========================
    // GET ALL USERS
    // ========================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ========================
    // GET USER BY ID
    // ========================
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // ========================
    // ADD USER (POST)
    // ========================
    public User addUser(User user) {
        // Toujours hasher le mot de passe avant enregistrement
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(encodePassword(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // ========================
    // UPDATE USER (PUT)
    // ========================
    public User updateUser(User user) {
        // Si le mot de passe a été modifié, on le re-hashe
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(encodePassword(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // ========================
    // DELETE USER
    // ========================
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ========================
    // HASH DU MOT DE PASSE
    // ========================
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
