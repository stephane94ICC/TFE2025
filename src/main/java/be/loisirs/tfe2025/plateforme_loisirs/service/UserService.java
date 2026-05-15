package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET USER BY ID
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // ADD USER
    public User addUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(encodePassword(user.getPassword()));
        }

        /*
         * Si l'utilisateur est créé par l'admin, il n'a pas accepté lui-même le RGPD.
         * On met donc false par défaut pour éviter une valeur null en base.
         */
        if (user.getConsentRgpd() == null) {
            user.setConsentRgpd(false);
        }

        return userRepository.save(user);
    }

    // UPDATE USER
    public User updateUser(User user) {
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

        /*
         * Le consentement RGPD n'est pas modifié par l'admin.
         * Il reste celui donné par l'utilisateur lors de l'inscription.
         */

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(encodePassword(user.getPassword()));
        }

        return userRepository.save(existing);
    }

    // DELETE USER
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // HASH DU MOT DE PASSE
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}