package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ReceiptNotAvailableException;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Address;
import be.loisirs.tfe2025.plateforme_loisirs.entity.AddressType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.DocumentType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.PartnerDocument;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.repository.AddressRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerDocumentRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerReceiptPdfRenderer.Kind;
import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerReceiptPdfRenderer.ReceiptData;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Documents de vente d'une réservation, émis au nom du partenaire :
 * - une facture si l'adresse du client a été figée au paiement : numérotée, enregistrée ;
 * - sinon un justificatif de paiement : ni numéroté ni enregistré, identifié par la
 *   référence de la réservation, avec la mention « Ce document n'est pas une facture » ;
 * - une note de crédit qui annule la facture d'une vente remboursée : numérotée dans
 *   la même série, enregistrée.
 *
 * Les documents sont émis au téléchargement par le partenaire, jamais pendant
 * l'annulation par le membre : Stripe a déjà remboursé à ce moment-là, et un échec
 * du PDF (siège manquant...) ne doit jamais défaire l'enregistrement du remboursement.
 */
@Service
public class PartnerDocumentService {

    // L'année du numéro et la date d'émission suivent l'heure belge, quel que soit le serveur.
    private static final ZoneId BRUSSELS = ZoneId.of("Europe/Brussels");
    private static final String BELGIUM = "Belgique";
    private static final String BELGIUM_CODE = "BE";

    private final ReservationRepository reservationRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerDocumentRepository documentRepository;
    private final AddressRepository addressRepository;
    private final PartnerReceiptPdfRenderer renderer;
    private final MessageSource messageSource;

    public PartnerDocumentService(ReservationRepository reservationRepository,
                                  PartnerRepository partnerRepository,
                                  PartnerDocumentRepository documentRepository,
                                  AddressRepository addressRepository,
                                  PartnerReceiptPdfRenderer renderer,
                                  MessageSource messageSource) {
        this.reservationRepository = reservationRepository;
        this.partnerRepository = partnerRepository;
        this.documentRepository = documentRepository;
        this.addressRepository = addressRepository;
        this.renderer = renderer;
        this.messageSource = messageSource;
    }

    /** Document prêt à télécharger ; documentNumber est null pour un justificatif. */
    public record Receipt(String documentNumber, String fileName, byte[] content) {
    }

    /**
     * READ_COMMITTED : sous MySQL, le niveau par défaut (REPEATABLE READ) fige la vue
     * des données à la première lecture de la transaction. Après avoir attendu le verrou
     * du partenaire, on ne verrait donc pas le numéro qu'une émission simultanée vient
     * d'enregistrer. En READ_COMMITTED, chaque lecture voit les données validées.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Receipt getReceipt(Long reservationId, String partnerEmail, Locale locale) {
        // Cloisonnement : la vente d'un autre partenaire est introuvable (404).
        Reservation reservation = reservationRepository
                .findByIdAndSession_Activity_Partner_User_Email(reservationId, partnerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        // 1. Facture déjà émise : le PDF enregistré, à l'identique (langue d'émission comprise).
        Optional<PartnerDocument> invoice = documentRepository
                .findByReservation_IdAndDocumentType(reservationId, DocumentType.INVOICE);
        if (invoice.isPresent()) {
            return stored(invoice.get(), locale);
        }

        Partner partner = reservation.getSession().getActivity().getPartner();

        // 2. Adresse du client connue : la facture est émise maintenant, numérotée et enregistrée.
        if (PartnerReservationService.isInvoiceAvailable(reservation)) {
            Address legalAddress = findLegalAddress(partner);
            return stored(issueInvoice(reservation, partner, legalAddress, locale), locale);
        }

        // 3. Vente payée sans adresse : justificatif, ni numéroté ni enregistré.
        if (PartnerReservationService.isReceiptAvailable(reservation)) {
            Address legalAddress = findLegalAddress(partner);
            LocalDateTime now = LocalDateTime.now(BRUSSELS).truncatedTo(ChronoUnit.SECONDS);
            byte[] content = render(Kind.RECEIPT, null, now, reservation, partner, legalAddress, locale, null);
            String fileName = label("receipt.fileName", locale) + "-" + reservation.getReference() + ".pdf";

            return new Receipt(null, fileName, content);
        }

        throw new ReceiptNotAvailableException(
                ReceiptNotAvailableException.RECEIPT_NOT_AVAILABLE,
                "Document impossible : vente sans TVA ou sans date de paiement figées.");
    }

    /**
     * Note de crédit d'une vente remboursée : elle annule la facture émise pour cette vente.
     * Même transaction, même verrou et même série de numéros que la facture.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Receipt getCreditNote(Long reservationId, String partnerEmail, Locale locale) {
        // Cloisonnement : la vente d'un autre partenaire est introuvable (404).
        Reservation reservation = reservationRepository
                .findByIdAndSession_Activity_Partner_User_Email(reservationId, partnerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));

        // Déjà émise : le PDF enregistré, à l'identique.
        Optional<PartnerDocument> creditNote = documentRepository
                .findByReservation_IdAndDocumentType(reservationId, DocumentType.CREDIT_NOTE);
        if (creditNote.isPresent()) {
            return stored(creditNote.get(), locale);
        }

        // Sans facture, rien à annuler : le remboursement suffit.
        PartnerDocument invoice = documentRepository
                .findByReservation_IdAndDocumentType(reservationId, DocumentType.INVOICE)
                .orElseThrow(() -> new ReceiptNotAvailableException(
                        ReceiptNotAvailableException.CREDIT_NOTE_NOT_AVAILABLE,
                        "Note de crédit impossible : aucune facture n'a été émise pour cette vente."));

        if (!PartnerReservationService.isRefunded(reservation)) {
            throw new ReceiptNotAvailableException(
                    ReceiptNotAvailableException.CREDIT_NOTE_NOT_AVAILABLE,
                    "Note de crédit impossible : la vente n'a pas été remboursée.");
        }

        Partner partner = reservation.getSession().getActivity().getPartner();
        Address legalAddress = findLegalAddress(partner);

        return stored(issueCreditNote(invoice, reservation, partner, legalAddress, locale), locale);
    }

    private PartnerDocument issueInvoice(Reservation reservation,
                                         Partner partner,
                                         Address legalAddress,
                                         Locale locale) {
        // Verrou sur la ligne du partenaire : ses attributions de numéros passent
        // une à une. Un second clic simultané attend ici la fin du premier.
        partnerRepository.findWithLockById(partner.getId());

        // Relecture après le verrou : le clic précédent a peut-être émis cette facture.
        Optional<PartnerDocument> existing = documentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.INVOICE);
        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDateTime now = LocalDateTime.now(BRUSSELS).truncatedTo(ChronoUnit.SECONDS);
        int year = now.getYear();
        int next = documentRepository.findLastSequenceNumber(partner.getId(), year) + 1;
        String number = PartnerDocument.formatNumber(partner.getId(), year, next);

        // Le PDF porte le numéro : il est produit avant l'enregistrement, puis figé avec lui.
        byte[] content = render(Kind.INVOICE, number, now, reservation, partner, legalAddress, locale, null);

        return documentRepository.save(
                PartnerDocument.invoice(partner, reservation, year, next, now, content));
    }

    private PartnerDocument issueCreditNote(PartnerDocument invoice,
                                            Reservation reservation,
                                            Partner partner,
                                            Address legalAddress,
                                            Locale locale) {
        partnerRepository.findWithLockById(partner.getId());

        // Relecture après le verrou : le clic précédent a peut-être émis cette note de crédit.
        Optional<PartnerDocument> existing = documentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.CREDIT_NOTE);
        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDateTime now = LocalDateTime.now(BRUSSELS).truncatedTo(ChronoUnit.SECONDS);
        int year = now.getYear();
        int next = documentRepository.findLastSequenceNumber(partner.getId(), year) + 1;
        String number = PartnerDocument.formatNumber(partner.getId(), year, next);

        byte[] content = render(Kind.CREDIT_NOTE, number, now, reservation, partner, legalAddress, locale, invoice);

        return documentRepository.save(PartnerDocument.creditNote(invoice, year, next, now, content));
    }

    private Receipt stored(PartnerDocument document, Locale locale) {
        String word = document.getDocumentType() == DocumentType.CREDIT_NOTE
                ? label("receipt.creditNoteFileName", locale)
                : label("receipt.invoiceFileName", locale);
        String fileName = word + "-" + document.getDocumentNumber() + ".pdf";

        return new Receipt(document.getDocumentNumber(), fileName, document.getContent());
    }

    /** Siège du partenaire (adresse LEGAL) ; s'il en a plusieurs, la première enregistrée. */
    private Address findLegalAddress(Partner partner) {
        return addressRepository.findByPartnerIdAndAddressType(partner.getId(), AddressType.LEGAL)
                .stream()
                .min(Comparator.comparing(Address::getId))
                .orElseThrow(() -> new ReceiptNotAvailableException(
                        ReceiptNotAvailableException.LEGAL_ADDRESS_MISSING,
                        "Ajoutez l'adresse de votre siège dans votre profil."));
    }

    private byte[] render(Kind kind,
                          String documentNumber,
                          LocalDateTime issuedAt,
                          Reservation reservation,
                          Partner partner,
                          Address legalAddress,
                          Locale locale,
                          PartnerDocument creditedInvoice) {
        ReceiptData data = toReceiptData(kind, documentNumber, issuedAt, reservation, partner,
                legalAddress, locale, creditedInvoice);

        return renderer.render(data, (key, args) -> messageSource.getMessage(key, args, locale));
    }

    private ReceiptData toReceiptData(Kind kind,
                                      String documentNumber,
                                      LocalDateTime issuedAt,
                                      Reservation reservation,
                                      Partner partner,
                                      Address legalAddress,
                                      Locale locale,
                                      PartnerDocument creditedInvoice) {
        BigDecimal total = reservation.getTotalPrice();
        BigDecimal vatRate = reservation.getVatRate();
        BigDecimal vatFactor = BigDecimal.ONE.add(vatRate.movePointLeft(2));

        // Prix TVA comprise : même calcul que CommissionCalculator (HTVA = TVAC / (1 + taux)).
        BigDecimal totalExclVat = total.divide(vatFactor, 2, RoundingMode.HALF_UP);
        BigDecimal vatAmount = total.subtract(totalExclVat);

        // 4 décimales : prix unitaire × quantité retombe sur le total HTVA (36 € TVAC pour 2 = 14,876 € HTVA).
        BigDecimal unitPriceExclVat = total.divide(
                BigDecimal.valueOf(reservation.getQuantity()).multiply(vatFactor), 4, RoundingMode.HALF_UP);

        ActivitySession session = reservation.getSession();

        return new ReceiptData(
                kind,
                partner.getName(),
                partnerAddressLines(legalAddress, locale),
                formatEnterpriseNumber(partner.getEnterpriseNumber()),
                formatVatNumber(partner.getVatNumber()),
                documentNumber,
                issuedAt,
                reservation.getReference(),
                reservation.getBillingFirstName() + " " + reservation.getBillingLastName(),
                kind == Kind.RECEIPT ? List.of() : customerAddressLines(reservation, locale),
                session.getActivity().getTitle(),
                session.getStartAt(),
                reservation.getQuantity(),
                unitPriceExclVat,
                totalExclVat,
                vatRate,
                vatAmount,
                total,
                reservation.getConfirmedAt(),
                creditedInvoice == null ? null : creditedInvoice.getDocumentNumber(),
                creditedInvoice == null ? null : creditedInvoice.getIssuedAt(),
                reservation.getRefundedAt()
        );
    }

    /** Siège belge sur deux lignes ; le pays n'est ajouté que s'il n'est pas la Belgique. */
    private List<String> partnerAddressLines(Address address, Locale locale) {
        List<String> lines = new ArrayList<>();

        String street = address.getStreet() + " " + address.getHouseNumber();
        if (address.getBox() != null && !address.getBox().isBlank()) {
            street += " " + label("receipt.box", locale) + " " + address.getBox();
        }
        lines.add(street);
        lines.add(address.getPostalCode() + " " + address.getCity());

        if (address.getCountry() != null && !BELGIUM.equalsIgnoreCase(address.getCountry())) {
            lines.add(address.getCountry());
        }
        return lines;
    }

    /** Adresse du client telle que saisie dans Stripe ; pays en toutes lettres s'il n'est pas la Belgique. */
    private List<String> customerAddressLines(Reservation reservation, Locale locale) {
        List<String> lines = new ArrayList<>();
        lines.add(reservation.getBillingAddressLine1());

        if (reservation.getBillingAddressLine2() != null) {
            lines.add(reservation.getBillingAddressLine2());
        }

        String postalCode = reservation.getBillingPostalCode();
        lines.add(postalCode == null
                ? reservation.getBillingCity()
                : postalCode + " " + reservation.getBillingCity());

        if (!BELGIUM_CODE.equals(reservation.getBillingCountry())) {
            lines.add(Locale.of("", reservation.getBillingCountry()).getDisplayCountry(locale));
        }
        return lines;
    }

    private String label(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /** Numéro d'entreprise au format de la BCE : 0123.456.789. */
    static String formatEnterpriseNumber(String number) {
        if (number == null || !number.matches("\\d{10}")) {
            return number;
        }
        return number.substring(0, 4) + "." + number.substring(4, 7) + "." + number.substring(7);
    }

    /** Numéro de TVA belge : BE 0123.456.789. */
    static String formatVatNumber(String vatNumber) {
        if (vatNumber == null || !vatNumber.matches("BE\\d{10}")) {
            return vatNumber;
        }
        return "BE " + formatEnterpriseNumber(vatNumber.substring(2));
    }
}