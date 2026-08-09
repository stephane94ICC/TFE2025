package be.loisirs.tfe2025.plateforme_loisirs.api.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
