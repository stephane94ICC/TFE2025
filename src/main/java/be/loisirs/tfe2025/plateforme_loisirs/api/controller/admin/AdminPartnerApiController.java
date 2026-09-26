package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AdminPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.CommissionRateRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.AdminPartnerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Accès réservé au rôle ADMIN par SecurityConfig (/api/admin/**)
@RestController
@RequestMapping("/api/admin/partners")
public class AdminPartnerApiController {

    private final AdminPartnerService adminPartnerService;

    public AdminPartnerApiController(AdminPartnerService adminPartnerService) {
        this.adminPartnerService = adminPartnerService;
    }

    @GetMapping
    public List<AdminPartnerResponseDTO> getPartners() {
        return adminPartnerService.getPartners();
    }

    @PutMapping("/{id}/commission-rate")
    public ResponseEntity<AdminPartnerResponseDTO> updateCommissionRate(
            @PathVariable Long id,
            @Valid @RequestBody CommissionRateRequestDTO dto
    ) {
        return ResponseEntity.ok(adminPartnerService.updateCommissionRate(id, dto.getCommissionRate()));
    }

    @PostMapping("/{id}/payment-account")
    public ResponseEntity<AdminPartnerResponseDTO> createPaymentAccount(@PathVariable Long id) {
        return ResponseEntity.ok(adminPartnerService.createPaymentAccount(id));
    }
}