package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.service.PartnerDocumentService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Locale;
import java.util.Set;


@RestController
@RequestMapping("/api/partner/reservations")
public class PartnerDocumentApiController {

    private static final Set<String> LANGUAGES = Set.of("fr", "nl", "en");

    private final PartnerDocumentService partnerDocumentService;

    public PartnerDocumentApiController(PartnerDocumentService partnerDocumentService) {
        this.partnerDocumentService = partnerDocumentService;
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(Principal principal,
                                             @PathVariable Long id,
                                             @RequestParam(defaultValue = "fr") String lang) {
        return pdf(partnerDocumentService.getReceipt(id, principal.getName(), locale(lang)));
    }

    @GetMapping("/{id}/credit-note")
    public ResponseEntity<byte[]> getCreditNote(Principal principal,
                                                @PathVariable Long id,
                                                @RequestParam(defaultValue = "fr") String lang) {
        return pdf(partnerDocumentService.getCreditNote(id, principal.getName(), locale(lang)));
    }

    /** Langue de l'interface du partenaire ; toute autre valeur donne le français. */
    private Locale locale(String lang) {
        return LANGUAGES.contains(lang) ? Locale.of(lang) : Locale.FRENCH;
    }

    private ResponseEntity<byte[]> pdf(PartnerDocumentService.Receipt receipt) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(receipt.fileName()).build().toString())
                .body(receipt.content());
    }
}