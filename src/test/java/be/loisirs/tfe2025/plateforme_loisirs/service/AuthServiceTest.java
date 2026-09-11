package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.InvalidCredentialsException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.AuthResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.LoginRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.RoleRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de AuthService.login.
 * Verrouille le correctif du 05/09 (compte desactive) et, surtout,
 * l'ORDRE des gardes : mot de passe avant etat du compte.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService.login")
class AuthServiceTest {

    private static final String EMAIL = "stromae.cowboy@example.com";
    private static final Long USER_ID = 16L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private AuthService authService;

    private User membre;
    private LoginRequestDTO requete;

    @BeforeEach
    void preparerLeMembre() {
        membre = new User();
        membre.setId(USER_ID);
        membre.setEmail(EMAIL);
        membre.setFirstName("Stromae");
        membre.setLastName("Cowboy");
        membre.setPassword("$2a$10$empreinte-du-vrai-mot-de-passe");
        membre.setProfileImageUrl("/uploads/members/photo-16.png");
        membre.setActive(true);
        membre.setRoles(new HashSet<>(Set.of(new Role(2L, "MEMBER"))));

        requete = new LoginRequestDTO(EMAIL, "MotDePasseCorrect1!");
    }

    @Test
    @DisplayName("Compte desactive, bon mot de passe : refus et AUCUN jeton emis")
    void compteDesactiveNeRecoitAucunJeton() {
        membre.setActive(false);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(requete))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Ce compte n'est plus actif.");

        // Le bug du 05/09 : un jeton etait delivre malgre le compte inactif.
        verifyNoInteractions(jwtService);
        verify(activityLogService).logForEmail(
                eq(ActivityEventType.ACCESS_DENIED_INACTIVE_ACCOUNT), eq(USER_ID), eq(EMAIL), anyString());
    }

    @Test
    @DisplayName("Compte desactive, MAUVAIS mot de passe : message generique, pas d'oracle")
    void mauvaisMotDePasseNeRevelePasLetatDuCompte() {
        membre.setActive(false);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(requete))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Adresse e-mail ou mot de passe incorrect.");

        // Sans le bon mot de passe, l'attaquant n'apprend rien sur l'existence
        // ni sur l'etat du compte : la garde du mot de passe passe en premier.
        verify(activityLogService, never()).logForEmail(
                eq(ActivityEventType.ACCESS_DENIED_INACTIVE_ACCOUNT), any(), any(), any());
        verify(activityLogService).logForEmail(
                eq(ActivityEventType.LOGIN_FAILURE), eq(USER_ID), eq(EMAIL), anyString());
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("E-mail inconnu : meme message que pour un mot de passe faux")
    void emailInconnuNePermetPasDenumererLesComptes() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(requete))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Adresse e-mail ou mot de passe incorrect.");

        verify(activityLogService).logForEmail(
                eq(ActivityEventType.LOGIN_FAILURE), isNull(), eq(EMAIL), anyString());
        verifyNoInteractions(jwtService, passwordEncoder);
    }

    @Test
    @DisplayName("Connexion valide : jeton emis et succes journalise")
    void connexionValideEmetUnJeton() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(membre)).thenReturn("jeton.de.test");

        AuthResponseDTO reponse = authService.login(requete);

        assertThat(reponse.getToken()).isEqualTo("jeton.de.test");
        assertThat(reponse.getEmail()).isEqualTo(EMAIL);
        assertThat(reponse.getRoles()).containsExactly("MEMBER");
        verify(activityLogService).logForEmail(
                eq(ActivityEventType.LOGIN_SUCCESS), eq(USER_ID), eq(EMAIL), isNull());
    }

}