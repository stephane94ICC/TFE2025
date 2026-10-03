package be.loisirs.tfe2025.plateforme_loisirs.api.exception;

public class RefundNotAvailableException extends RuntimeException {

    public static final String CODE = "REFUND_NOT_AVAILABLE";

    public RefundNotAvailableException(String message) {
        super(message);
    }
}