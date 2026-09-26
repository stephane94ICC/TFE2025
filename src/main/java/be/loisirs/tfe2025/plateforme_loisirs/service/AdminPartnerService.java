package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AdminPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/*
 * Gestion des partenaires par l'administrateur :
 * taux de commission du contrat et création du compte de paiement.
 */
@Service
public class AdminPartnerService {

    private static final Logger log = LoggerFactory.getLogger(AdminPartnerService.class);

    private final PartnerRepository partnerRepository;
    private final StripeConnectService stripeConnectService;

    public AdminPartnerService(PartnerRepository partnerRepository,
                               StripeConnectService stripeConnectService) {
        this.partnerRepository = partnerRepository;
        this.stripeConnectService = stripeConnectService;
    }

    @Transactional(readOnly = true)
    public List<AdminPartnerResponseDTO> getPartners() {
        return partnerRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /*
     * Le nouveau taux vaut pour les ventes futures uniquement :
     * chaque réservation garde le taux copié au moment de l'achat.
     */
    @Transactional
    public AdminPartnerResponseDTO updateCommissionRate(Long partnerId, BigDecimal commissionRate) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partenaire introuvable."));

        partner.setCommissionRate(commissionRate.setScale(2, RoundingMode.HALF_UP));

        return toDTO(partnerRepository.save(partner));
    }

    public AdminPartnerResponseDTO createPaymentAccount(Long partnerId) {
        return toDTO(stripeConnectService.createAccount(partnerId));
    }

    private AdminPartnerResponseDTO toDTO(Partner partner) {
        return new AdminPartnerResponseDTO(
                partner.getId(),
                partner.getName(),
                partner.getEmail(),
                partner.getVatNumber(),
                partner.getCommissionRate(),
                statusOrNull(partner)
        );
    }

    /*
     * Une panne de Stripe ne doit pas empêcher l'affichage de la liste :
     * l'état devient simplement « inconnu » pour ce partenaire.
     */
    private StripeConnectService.AccountStatus statusOrNull(Partner partner) {
        try {
            return stripeConnectService.getStatus(partner);
        } catch (IllegalStateException exception) {
            log.warn("État du compte Stripe indisponible pour le partenaire {}.", partner.getId());
            return null;
        }
    }
}