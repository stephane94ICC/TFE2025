package be.loisirs.tfe2025.plateforme_loisirs.api.exception;

/**
 * Document de vente refusé (409). Les causes sont distinguées par le code : le front
 * affiche le message traduit correspondant.
 */
public class ReceiptNotAvailableException extends RuntimeException {

    /** Vente sans TVA ou sans date de paiement figées (ventes de démonstration). */
    public static final String RECEIPT_NOT_AVAILABLE = "RECEIPT_NOT_AVAILABLE";

    /** Le partenaire n'a pas d'adresse de siège (type LEGAL). */
    public static final String LEGAL_ADDRESS_MISSING = "PARTNER_LEGAL_ADDRESS_MISSING";

    /** Note de crédit impossible : aucune facture émise, ou vente non remboursée. */
    public static final String CREDIT_NOTE_NOT_AVAILABLE = "CREDIT_NOTE_NOT_AVAILABLE";

    private final String code;

    public ReceiptNotAvailableException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}