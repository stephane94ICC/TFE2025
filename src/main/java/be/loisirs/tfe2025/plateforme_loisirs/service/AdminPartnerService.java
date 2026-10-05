package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.EmailAlreadyUsedException;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.EnterpriseNumberAlreadyUsedException;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AdminPartnerCreateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.partner.AdminPartnerResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import be.loisirs.tfe2025.plateforme_loisirs.util.EnterpriseNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;


@Service
public class AdminPartnerService {

    private static final Logger log = LoggerFactory.getLogger(AdminPartnerService.class);

    private static final String PARTNER_ROLE = "PARTNER";

    private final PartnerRepository partnerRepository;
    private final StripeConnectService stripeConnectService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public AdminPartnerService(PartnerRepository partnerRepository,
                               StripeConnectService stripeConnectService,
                               UserRepository userRepository,
                               UserService userService,
                               ActivityLogService activityLogService) {
        this.partnerRepository = partnerRepository;
        this.stripeConnectService = stripeConnectService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    @Transactional(readOnly = true)
    public List<AdminPartnerResponseDTO> getPartners() {
        return partnerRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public AdminPartnerResponseDTO createPartner(AdminPartnerCreateDTO dto) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String enterpriseNumber = EnterpriseNumber.normalize(dto.getEnterpriseNumber());

        // Doublons vérifiés avant l'insertion : message précis et traduisible.
        // Les contraintes UNIQUE restent le filet de sécurité (deux créations simultanées).
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Un compte existe déjà avec cette adresse e-mail.");
        }
        if (partnerRepository.existsByEnterpriseNumber(enterpriseNumber)) {
            throw new EnterpriseNumberAlreadyUsedException(
                    "Un partenaire existe déjà avec ce numéro d'entreprise.");
        }

        User user = new User();
        user.setEmail(email);
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setPassword(dto.getPassword()); // haché par addUser()
        user.setConsentRgpd(false);
        User savedUser = userService.addUser(user, PARTNER_ROLE);

        Partner partner = new Partner();
        partner.setUser(savedUser);
        partner.setName(dto.getName().trim());
        partner.setEnterpriseNumber(enterpriseNumber);
        partner.setVatNumber(EnterpriseNumber.toVatNumber(enterpriseNumber));
        partner.setPhone(blankToNull(dto.getPhone()));
        partner.setEmail(blankToNull(dto.getContactEmail()));
        partner.setWebsite(blankToNull(dto.getWebsite()));
        Partner savedPartner = partnerRepository.save(partner);

        // Jamais de mot de passe ni de donnée personnelle du compte dans le détail.
        activityLogService.log(
                ActivityEventType.PARTNER_CREATED,
                "PARTNER",
                savedPartner.getId(),
                "Partenaire créé : " + savedPartner.getName() + " (" + savedPartner.getVatNumber() + ")");

        log.info("Partenaire {} créé par l'admin (compte {}).", savedPartner.getId(), savedUser.getId());

        return toDTO(savedPartner);
    }

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

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }


    private StripeConnectService.AccountStatus statusOrNull(Partner partner) {
        try {
            return stripeConnectService.getStatus(partner);
        } catch (IllegalStateException exception) {
            log.warn("État du compte Stripe indisponible pour le partenaire {}.", partner.getId());
            return null;
        }
    }
}