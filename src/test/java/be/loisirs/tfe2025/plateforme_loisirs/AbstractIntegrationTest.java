package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySessionStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ActivitySessionRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.RoleRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Socle des tests d'integration.
 *
 * Porte les quatre annotations une seule fois : impossible d'oublier
 * le profil "test" et de taper dans la base de production.
 *
 * Transactionnel : chaque test s'annule a la fin. Seul le journal, en
 * REQUIRES_NEW, survit - c'est le comportement voulu en production.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {

    protected static final String PASSWORD = "MotDePasseTest1!";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;
    @Autowired
    protected ActivitySessionRepository activitySessionRepository;
    @Autowired
    protected ReservationRepository reservationRepository;

    /**
     * Cree un membre actif dont on connait le mot de passe en clair.
     * Les comptes du jeu de donnees ne conviennent pas : leurs empreintes
     * bcrypt ne correspondent a aucun mot de passe connu.
     */
    protected User createMember(String email) {
        Role memberRole = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new IllegalStateException(
                        "Role MEMBER absent : les migrations n'ont pas ete rejouees."));

        User member = new User();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(PASSWORD));
        member.setFirstName("Prenom");
        member.setLastName("Test");
        member.setConsentRgpd(true);
        member.setActive(true);
        member.setRoles(new HashSet<>(Set.of(memberRole)));

        return userRepository.save(member);
    }

    /**
     * Cree un creneau dans 30 jours, avec une date limite non depassee.
     *
     * L'activite et le lieu sont repris du jeu de donnees : ce sont des
     * donnees de reference stables. Les DATES, elles, sont calculees a
     * partir de maintenant - une session du jeu de donnees ferait expirer
     * le test le jour ou sa date serait passee.
     */
    protected ActivitySession createUpcomingSession() {
        ActivitySession template = activitySessionRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun creneau dans le jeu de donnees : migrations non rejouees."));

        LocalDateTime start = LocalDateTime.now().plusDays(30);

        ActivitySession session = new ActivitySession();
        session.setActivity(template.getActivity());
        session.setLocation(template.getLocation());
        session.setStartAt(start);
        session.setEndAt(start.plusHours(2));
        session.setCapacity(20);
        session.setStatus(ActivitySessionStatus.SCHEDULED);
        session.setBookingDeadline(start.minusHours(2));
        session.setCreatedAt(LocalDateTime.now());

        return activitySessionRepository.save(session);
    }

    /** Reservation confirmee, donc annulable : le chemin nominal du membre. */
    protected Reservation createConfirmedReservation(User member, ActivitySession session) {
        Reservation reservation = new Reservation();
        reservation.setUser(member);
        reservation.setSession(session);
        reservation.setQuantity(1);
        reservation.setTotalPrice(new BigDecimal("25.00"));
        reservation.setReference("TEST-" + UUID.randomUUID().toString().substring(0, 8));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setBookedAt(LocalDateTime.now());
        reservation.setConfirmedAt(LocalDateTime.now());

        // Instantane de facturation : NOT NULL depuis V12_1.
        reservation.setBillingFirstName(member.getFirstName());
        reservation.setBillingLastName(member.getLastName());
        reservation.setBillingEmail(member.getEmail());

        return reservationRepository.save(reservation);
    }

    /**
     * Obtient un VRAI jeton via la route de connexion.
     * Aucun raccourci : la chaine de filtres complete sera traversee.
     */
    protected String tokenFor(String email) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}";

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    /** "Bearer " est un mot litteral, suivi d'un espace, sans guillemets. */
    protected String bearer(String token) {
        return "Bearer " + token;
    }

}