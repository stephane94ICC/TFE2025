package be.loisirs.tfe2025.plateforme_loisirs.api.exception;

public class TooManyLoginAttemptsException extends RuntimeException {

    public static final String CODE = "TOO_MANY_LOGIN_ATTEMPTS";

    private final long retryAfterSeconds;

    public TooManyLoginAttemptsException(long retryAfterSeconds) {
        super("Trop de tentatives de connexion. Réessayez plus tard.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}