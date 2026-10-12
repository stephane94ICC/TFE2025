package be.loisirs.tfe2025.plateforme_loisirs.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerReservationResponseDTO {

    /** Document téléchargeable pour une vente : facture si l'adresse est connue, sinon justificatif. */
    public static final String INVOICE = "INVOICE";
    public static final String RECEIPT = "RECEIPT";

    private Long id;
    private String reference;
    private String activityTitle;
    private LocalDateTime sessionStartAt;
    private String customerName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime paidAt;
    private boolean receiptAvailable;

    /** INVOICE, RECEIPT, ou null si aucun document n'est possible (ventes de démonstration). */
    private String documentType;

    /** CONFIRMED, ou CANCELLED pour une vente remboursée qui a une facture. */
    private String status;

    /** Vente remboursée dont la facture doit être annulée : note de crédit téléchargeable. */
    private boolean creditNoteAvailable;
}