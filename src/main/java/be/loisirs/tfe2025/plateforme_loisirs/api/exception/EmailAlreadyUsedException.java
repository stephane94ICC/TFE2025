package be.loisirs.tfe2025.plateforme_loisirs.api.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
