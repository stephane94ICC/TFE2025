package be.loisirs.tfe2025.plateforme_loisirs.service;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Mise en page des documents de vente du partenaire (bibliothèque OpenPDF) :
 * facture, note de crédit qui l'annule, ou justificatif de paiement pour une vente
 * sans adresse de facturation.
 *
 * Ce composant ne lit ni la base ni la langue : il reçoit des valeurs déjà
 * calculées et des libellés déjà traduits. Il se teste donc sans base de données.
 * Polices standard (Helvetica, encodage Windows-1252) : accents français et
 * néerlandais et signe euro compris, sans fichier de police à embarquer.
 */
@Component
public class PartnerReceiptPdfRenderer {

    /** Nature du document : le titre et les mentions en dépendent. */
    public enum Kind {
        /** Vente sans adresse de facturation : pas de numéro, pas de valeur de facture. */
        RECEIPT,
        /** Facture : numérotée, émise au nom du partenaire, adresse du client comprise. */
        INVOICE,
        /** Note de crédit : annule une facture après remboursement ; montants en positif. */
        CREDIT_NOTE
    }

    /** Libellés traduits : clé des fichiers messages*.properties, avec arguments éventuels. */
    @FunctionalInterface
    public interface Labels {
        String get(String key, Object... args);
    }

    /**
     * Contenu du document, déjà calculé. Montants en euros ; unitPriceExclVat garde
     * 4 décimales pour que prix unitaire × quantité retombe sur le total HTVA.
     * Les trois derniers champs ne servent qu'à la note de crédit (null sinon).
     */
    public record ReceiptData(
            Kind kind,
            String partnerName,
            List<String> partnerAddressLines,
            String enterpriseNumber,
            String vatNumber,
            String documentNumber,
            LocalDateTime issuedAt,
            String reservationReference,
            String customerName,
            List<String> customerAddressLines,
            String activityTitle,
            LocalDateTime sessionStartAt,
            int quantity,
            BigDecimal unitPriceExclVat,
            BigDecimal totalExclVat,
            BigDecimal vatRate,
            BigDecimal vatAmount,
            BigDecimal totalPrice,
            LocalDateTime paidAt,
            String creditedDocumentNumber,
            LocalDateTime creditedIssuedAt,
            LocalDateTime refundedAt
    ) {
    }

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Bleu nuit de la charte (#1F3864) et fond clair des lignes.
    private static final Color BRAND = new Color(0x1F, 0x38, 0x64);
    private static final Color LIGHT = new Color(0xEE, 0xF1, 0xF6);

    public byte[] render(ReceiptData data, Labels labels) {
        // Polices créées à chaque appel : un objet Font n'est pas garanti
        // partageable entre deux téléchargements simultanés.
        Font title = new Font(Font.HELVETICA, 18, Font.BOLD, BRAND);
        Font partner = new Font(Font.HELVETICA, 13, Font.BOLD, BRAND);
        Font bold = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL);
        Font header = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);

        String documentTitle = switch (data.kind()) {
            case INVOICE -> labels.get("receipt.invoiceTitle");
            case CREDIT_NOTE -> labels.get("receipt.creditNoteTitle");
            case RECEIPT -> labels.get("receipt.title");
        };

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, out);
        document.addTitle(documentTitle + " " + (data.documentNumber() != null
                ? data.documentNumber()
                : data.reservationReference()));
        document.addAuthor(data.partnerName());
        document.addCreator("Bel'Loisirs");
        document.open();

        // 1. En-tête : l'émetteur (le partenaire) à gauche, le document à droite.
        PdfPTable top = new PdfPTable(new float[]{1.3f, 1f});
        top.setWidthPercentage(100);

        PdfPCell issuer = borderless();
        issuer.addElement(new Paragraph(data.partnerName(), partner));
        for (String line : data.partnerAddressLines()) {
            issuer.addElement(new Paragraph(line, normal));
        }
        issuer.addElement(new Paragraph(
                labels.get("receipt.enterpriseNumber") + " : " + data.enterpriseNumber(), normal));
        issuer.addElement(new Paragraph(
                labels.get("receipt.vatNumber") + " : " + data.vatNumber(), normal));
        top.addCell(issuer);

        PdfPCell doc = borderless();
        doc.addElement(right(new Paragraph(documentTitle, title)));
        if (data.documentNumber() != null) {
            doc.addElement(right(new Paragraph(
                    labels.get("receipt.number") + " " + data.documentNumber(), bold)));
        }
        doc.addElement(right(new Paragraph(
                labels.get("receipt.issuedAt") + " : " + data.issuedAt().format(DATE), normal)));
        top.addCell(doc);

        document.add(top);

        // 2. Client (avec son adresse sur une facture) et réservation.
        Paragraph customer = new Paragraph();
        customer.setSpacingBefore(24);
        customer.add(new Phrase(labels.get("receipt.customer") + " : ", bold));
        customer.add(new Phrase(data.customerName(), normal));
        document.add(customer);

        for (String line : data.customerAddressLines()) {
            document.add(new Paragraph(line, normal));
        }

        Paragraph reservation = new Paragraph();
        reservation.setSpacingBefore(6);
        reservation.add(new Phrase(labels.get("receipt.reservation") + " : ", bold));
        reservation.add(new Phrase(data.reservationReference(), normal));
        document.add(reservation);

        if (data.kind() == Kind.CREDIT_NOTE) {
            // Mention obligatoire : la facture annulée, par son numéro et sa date.
            Paragraph credits = new Paragraph(labels.get("receipt.credits",
                    data.creditedDocumentNumber(), data.creditedIssuedAt().format(DATE)), bold);
            credits.setSpacingBefore(6);
            document.add(credits);
        }

        // 3. Ligne vendue, hors TVA.
        PdfPTable lines = new PdfPTable(new float[]{2.8f, 0.9f, 2f, 1.6f});
        lines.setWidthPercentage(100);
        lines.setSpacingBefore(18);
        lines.setHeaderRows(1);

        lines.addCell(headerCell(labels.get("receipt.description"), header, Element.ALIGN_LEFT));
        lines.addCell(headerCell(labels.get("receipt.quantity"), header, Element.ALIGN_RIGHT));
        lines.addCell(headerCell(labels.get("receipt.unitPriceExclVat"), header, Element.ALIGN_RIGHT));
        lines.addCell(headerCell(labels.get("receipt.totalExclVat"), header, Element.ALIGN_RIGHT));

        PdfPCell description = bodyCell(Element.ALIGN_LEFT);
        description.addElement(new Paragraph(data.activityTitle(), bold));
        description.addElement(new Paragraph(
                labels.get("receipt.slot", data.sessionStartAt().format(DATE_TIME)), normal));
        lines.addCell(description);
        lines.addCell(valueCell(String.valueOf(data.quantity()), normal));
        lines.addCell(valueCell(preciseAmount(data.unitPriceExclVat()), normal));
        lines.addCell(valueCell(amount(data.totalExclVat()), normal));

        document.add(lines);

        // 4. Totaux, alignés à droite.
        PdfPTable totals = new PdfPTable(new float[]{1.6f, 1f});
        totals.setWidthPercentage(45);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setSpacingBefore(12);

        totals.addCell(totalCell(labels.get("receipt.totalExclVat"), normal, false));
        totals.addCell(totalCell(amount(data.totalExclVat()), normal, false));
        totals.addCell(totalCell(labels.get("receipt.vat", percent(data.vatRate())), normal, false));
        totals.addCell(totalCell(amount(data.vatAmount()), normal, false));
        totals.addCell(totalCell(labels.get("receipt.total"), bold, true));
        totals.addCell(totalCell(amount(data.totalPrice()), bold, true));

        document.add(totals);

        // 5. Paiement (ou remboursement) et mentions.
        Paragraph paid = new Paragraph(data.kind() == Kind.CREDIT_NOTE
                ? labels.get("receipt.refundedOnline", data.refundedAt().format(DATE))
                : labels.get("receipt.paidOnline", data.paidAt().format(DATE)), normal);
        paid.setSpacingBefore(24);
        document.add(paid);

        if (data.kind() == Kind.RECEIPT) {
            Paragraph notInvoice = new Paragraph(labels.get("receipt.notInvoice"), bold);
            notInvoice.setSpacingBefore(30);
            document.add(notInvoice);
        }

        document.close();
        return out.toByteArray();
    }

    /** Montant au format belge : 1234,50 € (même format dans les trois langues, comme le site). */
    static String amount(BigDecimal value) {
        return String.format(Locale.ROOT, "%.2f", value).replace('.', ',') + " €";
    }

    /** Prix unitaire : au moins 2 décimales, jusqu'à 4 si nécessaire (14,876 €). */
    static String preciseAmount(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        if (stripped.scale() < 2) {
            stripped = stripped.setScale(2);
        }
        return stripped.toPlainString().replace('.', ',') + " €";
    }

    /** Taux sans décimales inutiles : 21.00 donne 21, 6.00 donne 6. */
    static String percent(BigDecimal rate) {
        return rate.stripTrailingZeros().toPlainString();
    }

    private static Paragraph right(Paragraph paragraph) {
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        return paragraph;
    }

    private static PdfPCell borderless() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        return cell;
    }

    private static PdfPCell headerCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BRAND);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(6);
        return cell;
    }

    private static PdfPCell bodyCell(int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(LIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(6);
        cell.setPaddingTop(2);
        return cell;
    }

    private static PdfPCell valueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(LIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(6);
        return cell;
    }

    private static PdfPCell totalCell(String text, Font font, boolean last) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(last ? Rectangle.TOP : Rectangle.NO_BORDER);
        cell.setBorderColor(BRAND);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(4);
        return cell;
    }
}