package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.TooManyLoginAttemptsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test unitaire du limiteur, sans Spring ni base de données.
 * L'horloge est simulée : les paliers de 15 min, 1 h et l'oubli à 24 h
 * sont vérifiés sans attendre.
 */
@DisplayName("Limitation des essais de connexion")
class LoginAttemptLimiterTest {

    private static final String EMAIL = "cible@belloisirs.test";
    private static final String IP_ATTAQUANT = "10.0.0.66";
    private static final String IP_PROPRIETAIRE = "10.0.0.1";

    /** Horloge que le test fait avancer à la main. */
    private static final class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-10-05T10:00:00Z");

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }

    private MutableClock clock;
    private LoginAttemptLimiter limiter;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();
        limiter = new LoginAttemptLimiter(clock);
    }

    private void fail(String email, String ip, int times) {
        for (int i = 0; i < times; i++) {
            limiter.recordFailure(email, ip);
        }
    }

    private void assertBlocked(String email, String ip, long expectedSeconds) {
        assertThatThrownBy(() -> limiter.checkAllowed(email, ip))
                .isInstanceOf(TooManyLoginAttemptsException.class)
                .extracting("retryAfterSeconds")
                .isEqualTo(expectedSeconds);
    }

    private void assertAllowed(String email, String ip) {
        assertThatCode(() -> limiter.checkAllowed(email, ip)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("4 échecs : autorisé ; le 5e déclenche un blocage de 15 min, signalé une seule fois")
    void fifthFailureBlocksFifteenMinutes() {
        fail(EMAIL, IP_ATTAQUANT, 4);
        assertAllowed(EMAIL, IP_ATTAQUANT);

        List<String> started = limiter.recordFailure(EMAIL, IP_ATTAQUANT);

        assertThat(started).containsExactly("Compte + IP : blocage de 15 min");
        assertBlocked(EMAIL, IP_ATTAQUANT, 15 * 60);
    }

    @Test
    @DisplayName("Paliers : 15 min, puis 1 h, puis 1 h à nouveau (plafond)")
    void blocksEscalateThenCapAtOneHour() {
        fail(EMAIL, IP_ATTAQUANT, 5);
        assertBlocked(EMAIL, IP_ATTAQUANT, 15 * 60);

        clock.advance(Duration.ofMinutes(15));
        assertAllowed(EMAIL, IP_ATTAQUANT);
        fail(EMAIL, IP_ATTAQUANT, 5);
        assertBlocked(EMAIL, IP_ATTAQUANT, 60 * 60);

        clock.advance(Duration.ofHours(1));
        assertAllowed(EMAIL, IP_ATTAQUANT);
        fail(EMAIL, IP_ATTAQUANT, 5);
        assertBlocked(EMAIL, IP_ATTAQUANT, 60 * 60);
    }

    @Test
    @DisplayName("Le propriétaire, depuis une autre IP, n'est pas bloqué")
    void ownerFromAnotherIpIsNotBlocked() {
        fail(EMAIL, IP_ATTAQUANT, 5);

        assertBlocked(EMAIL, IP_ATTAQUANT, 15 * 60);
        assertAllowed(EMAIL, IP_PROPRIETAIRE);
    }

    @Test
    @DisplayName("Changer la casse de l'e-mail ne remet pas le compteur à zéro")
    void emailCaseDoesNotResetCounter() {
        fail("Cible@BelLoisirs.test", IP_ATTAQUANT, 2);
        fail("CIBLE@BELLOISIRS.TEST", IP_ATTAQUANT, 2);
        fail(EMAIL, IP_ATTAQUANT, 1);

        assertBlocked(EMAIL, IP_ATTAQUANT, 15 * 60);
    }

    @Test
    @DisplayName("Une connexion réussie remet à zéro le compteur compte + IP")
    void successResetsAccountCounter() {
        fail(EMAIL, IP_PROPRIETAIRE, 4);
        limiter.recordSuccess(EMAIL, IP_PROPRIETAIRE);
        fail(EMAIL, IP_PROPRIETAIRE, 4);

        assertAllowed(EMAIL, IP_PROPRIETAIRE);
    }

    @Test
    @DisplayName("IP seule : 20 échecs sur des comptes différents bloquent l'IP pour tous les comptes")
    void ipCounterBlocksPasswordSpraying() {
        for (int i = 0; i < 20; i++) {
            limiter.recordFailure("compte" + i + "@belloisirs.test", IP_ATTAQUANT);
        }

        assertBlocked("nouveau@belloisirs.test", IP_ATTAQUANT, 15 * 60);
        assertAllowed("nouveau@belloisirs.test", IP_PROPRIETAIRE);
    }

    @Test
    @DisplayName("Une connexion réussie NE remet PAS à zéro le compteur IP seule")
    void successDoesNotResetIpCounter() {
        for (int i = 0; i < 19; i++) {
            limiter.recordFailure("compte" + i + "@belloisirs.test", IP_ATTAQUANT);
        }

        // L'attaquant se connecte à son propre compte pour tenter d'effacer ses essais.
        limiter.recordSuccess("attaquant@belloisirs.test", IP_ATTAQUANT);
        limiter.recordFailure("compte19@belloisirs.test", IP_ATTAQUANT);

        assertBlocked("attaquant@belloisirs.test", IP_ATTAQUANT, 15 * 60);
    }

    @Test
    @DisplayName("24 h sans échec : le compteur est oublié, puis purgé de la mémoire")
    void entriesAreForgottenAfterTwentyFourHours() {
        fail(EMAIL, IP_ATTAQUANT, 4);

        clock.advance(Duration.ofHours(24).plusSeconds(1));
        fail(EMAIL, IP_ATTAQUANT, 4);
        assertAllowed(EMAIL, IP_ATTAQUANT);

        clock.advance(Duration.ofHours(24).plusSeconds(1));
        limiter.purgeForgottenEntries();
        assertThat(limiter.trackedEntries()).isZero();
    }
}