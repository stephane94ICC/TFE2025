package be.loisirs.tfe2025.plateforme_loisirs.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Locale;


//modifiable par Hibernate (updatable = false). Le numéro est calculé une seule fois

@Entity
@Table(name = "partner_document")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // réservé à Hibernate
public class PartnerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, updatable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, updatable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, updatable = false, length = 20)
    private DocumentType documentType;

    // Facture annulée par cette note de crédit ; null pour une facture (CHECK V18_2).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credited_document_id", updatable = false)
    private PartnerDocument creditedDocument;

    @Column(name = "issue_year", nullable = false, updatable = false)
    private Integer issueYear;

    @Column(name = "sequence_number", nullable = false, updatable = false)
    private Integer sequenceNumber;

    @Column(name = "document_number", nullable = false, unique = true, updatable = false, length = 30)
    private String documentNumber;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    // Le PDF tel qu'émis : un nouveau téléchargement renvoie exactement ce fichier.
    @Lob
    @Column(name = "content", nullable = false, updatable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] content;

    private PartnerDocument(Partner partner,
                            Reservation reservation,
                            DocumentType documentType,
                            PartnerDocument creditedDocument,
                            int issueYear,
                            int sequenceNumber,
                            LocalDateTime issuedAt,
                            byte[] content) {
        this.partner = partner;
        this.reservation = reservation;
        this.documentType = documentType;
        this.creditedDocument = creditedDocument;
        this.issueYear = issueYear;
        this.sequenceNumber = sequenceNumber;
        this.issuedAt = issuedAt;
        this.content = content;
        this.documentNumber = formatNumber(partner.getId(), issueYear, sequenceNumber);
    }

    /** Facture d'une vente : le numéro doit avoir été attribué sous le verrou du partenaire. */
    public static PartnerDocument invoice(Partner partner,
                                          Reservation reservation,
                                          int issueYear,
                                          int sequenceNumber,
                                          LocalDateTime issuedAt,
                                          byte[] content) {
        return new PartnerDocument(partner, reservation, DocumentType.INVOICE, null,
                issueYear, sequenceNumber, issuedAt, content);
    }

    /** Note de crédit qui annule une facture : même partenaire, même vente, même série de numéros. */
    public static PartnerDocument creditNote(PartnerDocument invoice,
                                             int issueYear,
                                             int sequenceNumber,
                                             LocalDateTime issuedAt,
                                             byte[] content) {
        return new PartnerDocument(invoice.getPartner(), invoice.getReservation(), DocumentType.CREDIT_NOTE,
                invoice, issueYear, sequenceNumber, issuedAt, content);
    }


    public static String formatNumber(Long partnerId, int issueYear, int sequenceNumber) {
        return String.format(Locale.ROOT, "%d-%d-%04d", partnerId, issueYear, sequenceNumber);
    }
}