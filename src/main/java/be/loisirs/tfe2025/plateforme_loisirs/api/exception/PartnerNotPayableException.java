package be.loisirs.tfe2025.plateforme_loisirs.api.exception;

public class PartnerNotPayableException extends RuntimeException {

    public static final String CODE = "PARTNER_NOT_PAYABLE";

    public PartnerNotPayableException(String message) {
        super(message);
    }
}