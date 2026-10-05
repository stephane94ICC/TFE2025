package be.loisirs.tfe2025.plateforme_loisirs.api.exception;


public class EnterpriseNumberAlreadyUsedException extends RuntimeException {

    public static final String CODE = "ENTERPRISE_NUMBER_ALREADY_USED";

    public EnterpriseNumberAlreadyUsedException(String message) {
        super(message);
    }
}