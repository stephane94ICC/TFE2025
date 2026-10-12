package be.loisirs.tfe2025.plateforme_loisirs;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ReceiptNotAvailableException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.AddressType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.DocumentType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.PartnerDocument;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.AddressRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerDocumentRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * « Mes ventes » et documents de vente : le partenaire voit les ventes de ses activités,
 * et seulement celles-là. Il télécharge une facture numérotée quand l'adresse du client
 * a été figée au paiement, sinon un justificatif sans numéro ; après un remboursement,
 * une note de crédit annule la facture.
 *
 * Le créneau de test reprend une activité du jeu de données : son partenaire en est
 * donc le propriétaire. Les autres ventes de ce partenaire (jeu de données) peuvent
 * figurer dans la liste : chaque test cible sa propre réservation par sa référence.
 */
@DisplayName("Ventes du partenaire, facture, justificatif et note de crédit PDF")
class PartnerReservationIntegrationTest extends AbstractIntegrationTest {

    private static final String SALES_URL = "/api/partner/reservations";
    private static final ZoneId BRUSSELS = ZoneId.of("Europe/Brussels");

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerDocumentRepository partnerDocumentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ---------------------------------------------------------------- Mes ventes

    @Test
    @DisplayName("Le partenaire voit la vente de son activité, sans l'e-mail du client")
    void partnerSeesOwnSaleWithoutCustomerEmail() throws Exception {
        ActivitySession session = createUpcomingSession();
        User member = createMember("vente-client@belloisirs.test");
        Reservation reservation = createConfirmedReservation(member, session);

        String token = tokenFor(ownerOf(session).getEmail());

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(sale(reservation) + ".customerName").value("Prenom Test"))
                .andExpect(content().string(not(containsString(member.getEmail()))));
    }

    @Test
    @DisplayName("Une réservation en attente de paiement n'est pas une vente")
    void pendingReservationIsNotListed() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(
                createMember("vente-attente@belloisirs.test"), session);

        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setConfirmedAt(null);
        reservationRepository.saveAndFlush(reservation);

        String token = tokenFor(ownerOf(session).getEmail());

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(reservation.getReference()))));
    }

    @Test
    @DisplayName("Un autre partenaire ne voit pas cette vente")
    void otherPartnerDoesNotSeeTheSale() throws Exception {
        ActivitySession session = createUpcomingSession();
        createConfirmedReservation(createMember("vente-autre@belloisirs.test"), session);

        String token = tokenFor(createPartner("vente-voisin@belloisirs.test", "0999000101").getEmail());

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Un membre n'accède pas aux ventes : 403")
    void memberIsForbidden() throws Exception {
        String token = tokenFor(createMember("vente-membre@belloisirs.test").getEmail());

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Document possible seulement si la TVA est figée sur la vente")
    void receiptRequiresFrozenVatRate() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(
                createMember("vente-tva@belloisirs.test"), session);

        String token = tokenFor(ownerOf(session).getEmail());

        // Comme les ventes du jeu de démonstration : aucun taux de TVA figé.
        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(sale(reservation) + ".receiptAvailable").value(false));

        // Comme une vente payée en ligne : taux figé au paiement.
        reservation.setVatRate(new BigDecimal("21.00"));
        reservationRepository.saveAndFlush(reservation);

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(sale(reservation) + ".receiptAvailable").value(true));
    }

    @Test
    @DisplayName("Type de document : facture si l'adresse est figée, sinon justificatif")
    void documentTypeDependsOnBillingAddress() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation withAddress = invoicedSale(session, "vente-adresse@belloisirs.test");
        Reservation withoutAddress = paidSale(session, "vente-sans-adresse@belloisirs.test");

        String token = tokenFor(ownerOf(session).getEmail());

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(sale(withAddress) + ".documentType").value("INVOICE"))
                .andExpect(jsonPath(sale(withoutAddress) + ".documentType").value("RECEIPT"));
    }

    // ------------------------------------------------------------------- Facture

    @Test
    @DisplayName("Facture : numérotée, au nom du partenaire, avec l'adresse du client, enregistrée")
    void invoiceIsNumberedAndStored() throws Exception {
        ActivitySession session = createUpcomingSession();
        Partner partner = session.getActivity().getPartner();
        Reservation reservation = invoicedSale(session, "facture-client@belloisirs.test");
        String expected = nextNumber(partner);

        String token = tokenFor(ownerOf(session).getEmail());

        MvcResult result = mockMvc.perform(get(documentUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("facture-" + expected + ".pdf")))
                .andReturn();

        assertThat(pdfText(result))
                .contains("Facture", expected, partner.getName(), reservation.getReference(),
                        "Avenue Louise 54", "1050 Ixelles", "Prix unitaire HTVA")
                .doesNotContain("n'est pas une facture");

        Optional<PartnerDocument> invoice = partnerDocumentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.INVOICE);
        assertThat(invoice).isPresent();
        assertThat(invoice.get().getDocumentNumber()).isEqualTo(expected);
        assertThat(invoice.get().getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Facture : relue en base à l'identique, numéro suivant pour une autre vente")
    void invoiceIsStableAndSequential() throws Exception {
        ActivitySession session = createUpcomingSession();
        Partner partner = session.getActivity().getPartner();
        Reservation first = invoicedSale(session, "facture-un@belloisirs.test");
        Reservation second = invoicedSale(session, "facture-deux@belloisirs.test");
        int last = partnerDocumentRepository.findLastSequenceNumber(partner.getId(), currentYear());

        String token = tokenFor(ownerOf(session).getEmail());

        byte[] issued = download(first, token);

        // Vide le cache d'Hibernate : le second téléchargement relit le PDF dans MySQL.
        entityManager.flush();
        entityManager.clear();

        byte[] downloadedAgain = download(first, token);
        download(second, token);

        assertThat(downloadedAgain).isEqualTo(issued);
        assertThat(numberOf(first))
                .isEqualTo(PartnerDocument.formatNumber(partner.getId(), currentYear(), last + 1));
        assertThat(numberOf(second))
                .isEqualTo(PartnerDocument.formatNumber(partner.getId(), currentYear(), last + 2));
    }

    @Test
    @DisplayName("Facture : la langue est celle de l'émission, le document ne change plus ensuite")
    void invoiceLanguageIsFixedAtIssue() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = invoicedSale(session, "facture-langue@belloisirs.test");

        String token = tokenFor(ownerOf(session).getEmail());

        MvcResult dutch = mockMvc.perform(get(documentUrl(reservation) + "?lang=nl")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(pdfText(dutch)).contains("Factuur", "Eenheidsprijs excl. btw");

        MvcResult french = mockMvc.perform(get(documentUrl(reservation) + "?lang=fr")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(french.getResponse().getContentAsByteArray())
                .isEqualTo(dutch.getResponse().getContentAsByteArray());
    }

    // --------------------------------------------------------------- Justificatif

    @Test
    @DisplayName("Justificatif : vente sans adresse, ni numéroté ni enregistré, mention obligatoire")
    void receiptForSaleWithoutAddress() throws Exception {
        ActivitySession session = createUpcomingSession();
        Partner partner = session.getActivity().getPartner();
        Reservation reservation = paidSale(session, "justificatif-client@belloisirs.test");
        int last = partnerDocumentRepository.findLastSequenceNumber(partner.getId(), currentYear());

        String token = tokenFor(ownerOf(session).getEmail());

        MvcResult result = mockMvc.perform(get(documentUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("justificatif-" + reservation.getReference() + ".pdf")))
                .andReturn();

        assertThat(pdfText(result)).contains(
                "Justificatif de paiement",
                reservation.getReference(),
                "Ce document n'est pas une facture.");

        // Aucun numéro de la série des factures n'a été consommé.
        assertThat(partnerDocumentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.INVOICE)).isEmpty();
        assertThat(partnerDocumentRepository.findLastSequenceNumber(partner.getId(), currentYear()))
                .isEqualTo(last);
    }

    @Test
    @DisplayName("Justificatif : en néerlandais et en anglais ; langue inconnue = français")
    void receiptIsTranslated() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = paidSale(session, "justificatif-langue@belloisirs.test");

        String token = tokenFor(ownerOf(session).getEmail());

        MvcResult dutch = mockMvc.perform(get(documentUrl(reservation) + "?lang=nl")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("betalingsbewijs-")))
                .andReturn();
        assertThat(pdfText(dutch)).contains("Betalingsbewijs", "Dit document is geen factuur.");

        MvcResult english = mockMvc.perform(get(documentUrl(reservation) + "?lang=en")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(pdfText(english)).contains("Payment receipt", "This document is not an invoice.");

        mockMvc.perform(get(documentUrl(reservation) + "?lang=de")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("justificatif-")));
    }

    // ------------------------------------------------------------ Note de crédit

    @Test
    @DisplayName("Note de crédit : annule la facture après remboursement, numéro suivant de la série")
    void creditNoteCancelsInvoiceAfterRefund() throws Exception {
        ActivitySession session = createUpcomingSession();
        Partner partner = session.getActivity().getPartner();
        Reservation reservation = invoicedSale(session, "avoir-client@belloisirs.test");

        String token = tokenFor(ownerOf(session).getEmail());
        byte[] invoicePdf = download(reservation, token);
        String invoiceNumber = numberOf(reservation);
        String expected = nextNumber(partner);

        refundAsMember(reservation, "avoir-client@belloisirs.test");

        MvcResult result = mockMvc.perform(get(creditNoteUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        containsString("note-de-credit-" + expected + ".pdf")))
                .andReturn();

        assertThat(pdfText(result)).contains(
                "Note de crédit",
                expected,
                "Annule la facture n° " + invoiceNumber,
                "Remboursement effectué le");

        PartnerDocument creditNote = partnerDocumentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.CREDIT_NOTE)
                .orElseThrow();
        assertThat(creditNote.getDocumentNumber()).isEqualTo(expected);
        assertThat(creditNote.getCreditedDocument().getDocumentNumber()).isEqualTo(invoiceNumber);

        // La facture reste téléchargeable, inchangée, après le remboursement.
        assertThat(download(reservation, token)).isEqualTo(invoicePdf);
    }

    @Test
    @DisplayName("Note de crédit : refusée (409) sans facture émise, ou sans remboursement")
    void creditNoteRequiresInvoiceAndRefund() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation refundedWithoutInvoice = invoicedSale(session, "avoir-sans-facture@belloisirs.test");
        Reservation invoicedNotRefunded = invoicedSale(session, "avoir-sans-remboursement@belloisirs.test");

        String token = tokenFor(ownerOf(session).getEmail());

        refundAsMember(refundedWithoutInvoice, "avoir-sans-facture@belloisirs.test");
        download(invoicedNotRefunded, token);

        for (Reservation reservation : new Reservation[]{refundedWithoutInvoice, invoicedNotRefunded}) {
            mockMvc.perform(get(creditNoteUrl(reservation))
                            .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code")
                            .value(ReceiptNotAvailableException.CREDIT_NOTE_NOT_AVAILABLE));

            assertThat(partnerDocumentRepository.findByReservation_IdAndDocumentType(
                    reservation.getId(), DocumentType.CREDIT_NOTE)).isEmpty();
        }
    }

    @Test
    @DisplayName("Mes ventes : une vente remboursée avec facture reste listée, avec sa note de crédit")
    void refundedInvoicedSaleIsListed() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation invoiced = invoicedSale(session, "liste-avoir@belloisirs.test");
        Reservation notInvoiced = invoicedSale(session, "liste-sans-facture@belloisirs.test");

        String token = tokenFor(ownerOf(session).getEmail());
        download(invoiced, token);

        refundAsMember(invoiced, "liste-avoir@belloisirs.test");
        refundAsMember(notInvoiced, "liste-sans-facture@belloisirs.test");

        mockMvc.perform(get(SALES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(sale(invoiced) + ".status").value("CANCELLED"))
                .andExpect(jsonPath(sale(invoiced) + ".documentType").value("INVOICE"))
                .andExpect(jsonPath(sale(invoiced) + ".creditNoteAvailable").value(true))
                .andExpect(content().string(not(containsString(notInvoiced.getReference()))));
    }

    @Test
    @DisplayName("Note de crédit : 404 pour la vente d'un autre partenaire")
    void creditNoteOfOtherPartnerIsNotFound() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = invoicedSale(session, "avoir-voisin-client@belloisirs.test");

        String token = tokenFor(createPartner("avoir-voisin@belloisirs.test", "0999000103").getEmail());

        mockMvc.perform(get(creditNoteUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------- Refus

    @Test
    @DisplayName("Document : 404 pour la vente d'un autre partenaire, aucune facture émise")
    void otherPartnerGetsNotFound() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = invoicedSale(session, "facture-voisin-client@belloisirs.test");

        String token = tokenFor(createPartner("facture-voisin@belloisirs.test", "0999000102").getEmail());

        mockMvc.perform(get(documentUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound());

        assertThat(partnerDocumentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.INVOICE)).isEmpty();
    }

    @Test
    @DisplayName("Document : vente sans TVA figée refusée (409)")
    void saleWithoutFrozenVatIsRefused() throws Exception {
        ActivitySession session = createUpcomingSession();
        Reservation reservation = createConfirmedReservation(
                createMember("document-sans-tva@belloisirs.test"), session);

        String token = tokenFor(ownerOf(session).getEmail());

        mockMvc.perform(get(documentUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ReceiptNotAvailableException.RECEIPT_NOT_AVAILABLE));
    }

    @Test
    @DisplayName("Facture : partenaire sans adresse de siège refusé (409), aucun numéro attribué")
    void partnerWithoutLegalAddressIsRefused() throws Exception {
        ActivitySession session = createUpcomingSession();
        Partner partner = session.getActivity().getPartner();
        Reservation reservation = invoicedSale(session, "facture-sans-siege@belloisirs.test");

        // Supprimé dans la transaction du test : annulé à la fin.
        addressRepository.deleteAll(
                addressRepository.findByPartnerIdAndAddressType(partner.getId(), AddressType.LEGAL));
        addressRepository.flush();

        String token = tokenFor(ownerOf(session).getEmail());

        mockMvc.perform(get(documentUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ReceiptNotAvailableException.LEGAL_ADDRESS_MISSING));

        assertThat(partnerDocumentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.INVOICE)).isEmpty();
    }

    // ------------------------------------------------------------------ outils

    /** Filtre JsonPath : la ligne de la liste qui porte la référence de cette réservation. */
    private String sale(Reservation reservation) {
        return "$[?(@.reference == '" + reservation.getReference() + "')]";
    }

    private String documentUrl(Reservation reservation) {
        return SALES_URL + "/" + reservation.getId() + "/receipt";
    }

    private String creditNoteUrl(Reservation reservation) {
        return SALES_URL + "/" + reservation.getId() + "/credit-note";
    }

    /**
     * Le membre annule sa réservation par la vraie route : remboursement par le faux
     * composant Stripe du socle de tests, puis statut CANCELLED et date de remboursement.
     */
    private void refundAsMember(Reservation reservation, String customerEmail) throws Exception {
        String memberToken = tokenFor(customerEmail);

        mockMvc.perform(patch("/api/member/reservations/" + reservation.getId() + "/cancel")
                        .header(HttpHeaders.AUTHORIZATION, bearer(memberToken)))
                .andExpect(status().isNoContent());
    }

    /** Vente payée en ligne sans adresse : TVA et date de paiement figées (justificatif). */
    private Reservation paidSale(ActivitySession session, String customerEmail) {
        Reservation reservation = createConfirmedReservation(createMember(customerEmail), session);
        reservation.setVatRate(new BigDecimal("21.00"));
        return reservationRepository.saveAndFlush(reservation);
    }

    /** Vente payée avec l'adresse saisie dans Stripe, comme après le webhook (facture). */
    private Reservation invoicedSale(ActivitySession session, String customerEmail) {
        Reservation reservation = createConfirmedReservation(createMember(customerEmail), session);
        reservation.setVatRate(new BigDecimal("21.00"));
        reservation.setBillingAddressLine1("Avenue Louise 54");
        reservation.setBillingPostalCode("1050");
        reservation.setBillingCity("Ixelles");
        reservation.setBillingCountry("BE");
        return reservationRepository.saveAndFlush(reservation);
    }

    private byte[] download(Reservation reservation, String token) throws Exception {
        return mockMvc.perform(get(documentUrl(reservation))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
    }

    private int currentYear() {
        return LocalDate.now(BRUSSELS).getYear();
    }

    /** Numéro que recevra le prochain document fiscal du partenaire cette année. */
    private String nextNumber(Partner partner) {
        int last = partnerDocumentRepository.findLastSequenceNumber(partner.getId(), currentYear());
        return PartnerDocument.formatNumber(partner.getId(), currentYear(), last + 1);
    }

    private String numberOf(Reservation reservation) {
        return partnerDocumentRepository
                .findByReservation_IdAndDocumentType(reservation.getId(), DocumentType.INVOICE)
                .map(PartnerDocument::getDocumentNumber)
                .orElseThrow();
    }

    /** Vérifie la signature d'un PDF, puis en extrait le texte de la première page. */
    private String pdfText(MvcResult result) throws Exception {
        byte[] pdf = result.getResponse().getContentAsByteArray();
        assertThat(new String(pdf, 0, 4, StandardCharsets.ISO_8859_1)).isEqualTo("%PDF");

        return new PdfTextExtractor(new PdfReader(pdf)).getTextFromPage(1);
    }

    /**
     * Rend utilisable le partenaire propriétaire de l'activité du créneau : les comptes
     * du jeu de données ont des mots de passe inconnus. Le test est transactionnel :
     * ce changement est annulé à la fin. saveAndFlush : la connexion qui suit doit
     * lire le nouveau mot de passe en base.
     */
    private User ownerOf(ActivitySession session) {
        User owner = session.getActivity().getPartner().getUser();
        owner.setPassword(passwordEncoder.encode(PASSWORD));
        owner.setActive(true);

        return userRepository.saveAndFlush(owner);
    }

    /**
     * Partenaire créé pour le test, sans activité (même méthode que dans
     * PartnerPaymentAccountIntegrationTest). Numéros d'entreprise et de TVA
     * uniques : contraintes UNIQUE de la table partner.
     */
    private User createPartner(String email, String number) {
        Role partnerRole = roleRepository.findByName("PARTNER")
                .orElseThrow(() -> new IllegalStateException(
                        "Role PARTNER absent : les migrations n'ont pas ete rejouees."));

        User user = createMember(email);
        user.getRoles().add(partnerRole);
        userRepository.save(user);

        Partner partner = new Partner();
        partner.setUser(user);
        partner.setName("Partenaire de test " + number);
        partner.setEnterpriseNumber(number);
        partner.setVatNumber("BE" + number);
        partnerRepository.save(partner);

        return user;
    }
}