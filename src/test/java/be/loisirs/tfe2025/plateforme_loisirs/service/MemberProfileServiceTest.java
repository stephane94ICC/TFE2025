package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.InvalidCredentialsException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AccountDeletionRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires des gardes de suppression de compte (RGPD art. 17).
 * Aucun contexte Spring, aucune base : seules les regles metier sont eprouvees.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberProfileService.deleteAccount")
class MemberProfileServiceTest {

    private static final String EMAIL = "stromae.cowboy@example.com";
    private static final Long USER_ID = 16L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ActivityLogService activityLogService;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberProfileService memberProfileService;

    private User membre;
    private AccountDeletionRequestDTO requete;

    @BeforeEach
    void preparerLeMembre() {
        membre = new User();
        membre.setId(USER_ID);
        membre.setEmail(EMAIL);
        membre.setFirstName("Stromae");
        membre.setLastName("Cowboy");
        membre.setPhone("0470112233");
        membre.setPassword("$2a$10$empreinte-du-vrai-mot-de-passe");
        membre.setProfileImageUrl("/uploads/members/photo-16.png");
        membre.setActive(true);
        membre.setRoles(new HashSet<>(Set.of(new Role(2L, "MEMBER"))));

        requete = new AccountDeletionRequestDTO("MotDePasseCorrect1!");
    }

    @Test
    @DisplayName("Mot de passe incorrect : refus, et aucune ecriture")
    void motDePasseIncorrectNeToucheARien() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> memberProfileService.deleteAccount(EMAIL, requete))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Mot de passe incorrect");

        // L'identite doit etre intacte : un refus ne modifie rien.
        assertThat(membre.getEmail()).isEqualTo(EMAIL);
        assertThat(membre.getActive()).isTrue();
        verify(userRepository, never()).save(any());
        verifyNoInteractions(activityLogService, reservationRepository, imageStorageService);
    }

    @Test
    @DisplayName("Compte PARTNER : la suppression libre-service est refusee")
    void compteNonMembreRefuse() {
        membre.setRoles(new HashSet<>(Set.of(new Role(3L, "PARTNER"))));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> memberProfileService.deleteAccount(EMAIL, requete))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("réservée aux comptes membres");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(activityLogService);
    }

    @Test
    @DisplayName("Reservations CONFIRMED a venir : refus avec le decompte exact")
    void reservationsAVenirBloquentLaSuppression() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(reservationRepository.countUpcomingByUserEmailAndStatus(
                eq(EMAIL), eq(ReservationStatus.CONFIRMED), any(LocalDateTime.class)))
                .thenReturn(2L);

        assertThatThrownBy(() -> memberProfileService.deleteAccount(EMAIL, requete))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2 réservation(s) à venir");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(activityLogService);
    }

    @Test
    @DisplayName("Suppression valide : compte anonymise, journal sur l'adresse d'origine")
    void suppressionValideAnonymiseEtJournalise() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(membre));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$empreinte-aleatoire");
        when(reservationRepository.countUpcomingByUserEmailAndStatus(
                eq(EMAIL), eq(ReservationStatus.CONFIRMED), any(LocalDateTime.class)))
                .thenReturn(0L);

        memberProfileService.deleteAccount(EMAIL, requete);

        assertThat(membre.getEmail()).isEqualTo("anonyme-16@belloisirs.invalid");
        assertThat(membre.getFirstName()).isEqualTo("Membre");
        assertThat(membre.getLastName()).isEqualTo("supprimé");
        assertThat(membre.getPhone()).isNull();
        assertThat(membre.getActive()).isFalse();
        assertThat(membre.getPassword()).isEqualTo("$2a$10$empreinte-aleatoire");
        verify(userRepository).save(membre);

        // Les deux entrees portent l'adresse d'ORIGINE, et dans cet ordre :
        // journaliser apres anonymisation perdrait l'identite (cf. logForEmail).
        InOrder ordre = inOrder(activityLogService);
        ordre.verify(activityLogService).logForEmail(
                eq(ActivityEventType.ACCOUNT_DELETION_REQUESTED), eq(USER_ID), eq(EMAIL), anyString());
        ordre.verify(activityLogService).logForEmail(
                eq(ActivityEventType.ACCOUNT_ANONYMIZED), eq(USER_ID), eq(EMAIL), anyString());
    }

}