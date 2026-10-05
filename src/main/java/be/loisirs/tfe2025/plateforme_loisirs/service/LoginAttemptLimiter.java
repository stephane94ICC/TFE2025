package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.TooManyLoginAttemptsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class LoginAttemptLimiter {

    static final int ACCOUNT_IP_THRESHOLD = 5;
    static final int IP_THRESHOLD = 20;
    static final Duration FIRST_BLOCK = Duration.ofMinutes(15);
    static final Duration NEXT_BLOCK = Duration.ofHours(1);
    static final Duration FORGET_AFTER = Duration.ofHours(24);

    /** Valeur immuable : remplacée en bloc par compute(), donc sûre entre plusieurs requêtes simultanées. */
    private record Attempts(int failures, Instant lastFailure, Instant blockedUntil) {
    }

    private final Map<String, Attempts> byAccountAndIp = new ConcurrentHashMap<>();
    private final Map<String, Attempts> byIp = new ConcurrentHashMap<>();
    private final Clock clock;

    @Autowired
    public LoginAttemptLimiter() {
        this(Clock.systemUTC());
    }

    /** Horloge injectable : permet de tester les paliers sans attendre. */
    LoginAttemptLimiter(Clock clock) {
        this.clock = clock;
    }

    /**
     * À appeler AVANT de vérifier le mot de passe.
     *
     * @throws TooManyLoginAttemptsException si le couple compte + IP ou l'IP est bloqué
     */
    public void checkAllowed(String email, String ip) {
        Instant now = clock.instant();

        long remaining = Math.max(
                remainingSeconds(byAccountAndIp.get(accountKey(email, ip)), now),
                remainingSeconds(byIp.get(ipKey(ip)), now));

        if (remaining > 0) {
            throw new TooManyLoginAttemptsException(remaining);
        }
    }

    /**
     * Enregistre un échec sur les deux compteurs.
     *
     * @return la description des blocages qui COMMENCENT à cet échec (vide sinon),
     *         pour les journaliser une seule fois par blocage
     */
    public List<String> recordFailure(String email, String ip) {
        Instant now = clock.instant();
        List<String> startedBlocks = new ArrayList<>();

        Duration accountBlock = registerFailure(
                byAccountAndIp, accountKey(email, ip), ACCOUNT_IP_THRESHOLD, now);
        if (accountBlock != null) {
            startedBlocks.add("Compte + IP : blocage de " + accountBlock.toMinutes() + " min");
        }

        Duration ipBlock = registerFailure(byIp, ipKey(ip), IP_THRESHOLD, now);
        if (ipBlock != null) {
            startedBlocks.add("IP seule : blocage de " + ipBlock.toMinutes() + " min");
        }

        return startedBlocks;
    }

    /** Connexion réussie : seul le compteur compte + IP est remis à zéro. */
    public void recordSuccess(String email, String ip) {
        byAccountAndIp.remove(accountKey(email, ip));
    }

    /** Purge horaire des entrées oubliées : la mémoire ne grossit pas indéfiniment. */
    @Scheduled(fixedRate = 3_600_000)
    public void purgeForgottenEntries() {
        Instant now = clock.instant();
        byAccountAndIp.values().removeIf(attempts -> isForgotten(attempts, now));
        byIp.values().removeIf(attempts -> isForgotten(attempts, now));
    }

    /** Nombre d'entrées suivies (tests uniquement). */
    int trackedEntries() {
        return byAccountAndIp.size() + byIp.size();
    }

    private Duration registerFailure(Map<String, Attempts> counters, String key,
                                     int threshold, Instant now) {
        Duration[] startedBlock = new Duration[1];

        counters.compute(key, (k, current) -> {
            Attempts base = (current == null || isForgotten(current, now))
                    ? new Attempts(0, now, null)
                    : current;

            int failures = base.failures() + 1;
            Instant blockedUntil = base.blockedUntil();

            if (failures % threshold == 0) {
                Duration block = (failures == threshold) ? FIRST_BLOCK : NEXT_BLOCK;
                blockedUntil = now.plus(block);
                startedBlock[0] = block;
            }

            return new Attempts(failures, now, blockedUntil);
        });

        return startedBlock[0];
    }

    private static boolean isForgotten(Attempts attempts, Instant now) {
        return now.isAfter(attempts.lastFailure().plus(FORGET_AFTER));
    }

    private static long remainingSeconds(Attempts attempts, Instant now) {
        if (attempts == null || attempts.blockedUntil() == null
                || !now.isBefore(attempts.blockedUntil())) {
            return 0;
        }

        // Arrondi à la seconde supérieure : jamais « 0 seconde » pendant un blocage.
        return (Duration.between(now, attempts.blockedUntil()).toMillis() + 999) / 1000;
    }

    private static String accountKey(String email, String ip) {
        String normalizedEmail = (email == null) ? "" : email.trim().toLowerCase(Locale.ROOT);
        return normalizedEmail + "|" + ipKey(ip);
    }

    private static String ipKey(String ip) {
        return (ip == null) ? "inconnue" : ip;
    }
}