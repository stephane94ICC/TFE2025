package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Partner;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Comptes de paiement des partenaires (Stripe Connect, comptes Express).
 *
 * Répartition des rôles :
 *   - l'administrateur crée le compte : il décide qui peut vendre ;
 *   - le partenaire complète lui-même son inscription chez Stripe
 *     (identité, IBAN) : la plateforme ne manipule jamais ces données.
 *
 * L'état du compte n'est pas stocké en base : il est demandé à Stripe
 * au moment utile, il ne peut donc jamais être périmé.
 */
@Service
public class StripeConnectService {

    public enum AccountStatus {
        // Aucun compte créé par l'administrateur
        NOT_CREATED,
        // Compte créé, inscription du partenaire non terminée
        INCOMPLETE,
        // Inscription terminée, vérification en cours chez Stripe
        PENDING_VERIFICATION,
        // Le partenaire peut recevoir des paiements
        ACTIVE
    }

    private static final Logger log = LoggerFactory.getLogger(StripeConnectService.class);

    private static final String COUNTRY = "BE";

    private final PartnerRepository partnerRepository;
    private final String stripeSecretKey;
    private final String frontendUrl;

    public StripeConnectService(
            PartnerRepository partnerRepository,
            @Value("${stripe.secret-key}") String stripeSecretKey,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.partnerRepository = partnerRepository;
        this.stripeSecretKey = stripeSecretKey;
        this.frontendUrl = frontendUrl;
    }

    /*
     * Administrateur : crée le compte Stripe du partenaire.
     * Un seul compte par partenaire : un second appel est refusé.
     */
    @Transactional
    public Partner createAccount(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Partenaire introuvable."));

        if (partner.getStripeAccountId() != null) {
            throw new IllegalArgumentException("Ce partenaire a déjà un compte de paiement.");
        }

        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCountry(COUNTRY)
                .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
                .setEmail(contactEmail(partner))
                .setCapabilities(
                        AccountCreateParams.Capabilities.builder()
                                .setCardPayments(
                                        AccountCreateParams.Capabilities.CardPayments.builder()
                                                .setRequested(true)
                                                .build()
                                )
                                .setTransfers(
                                        AccountCreateParams.Capabilities.Transfers.builder()
                                                .setRequested(true)
                                                .build()
                                )
                                .build()
                )
                .putMetadata("partnerId", partner.getId().toString())
                .build();

        try {
            Stripe.apiKey = stripeSecretKey;
            Account account = Account.create(params);
            partner.setStripeAccountId(account.getId());
            return partnerRepository.save(partner);
        } catch (StripeException exception) {
            // Le message de Stripe est conservé dans les logs : sans lui, impossible de diagnostiquer
            log.error("Stripe a refusé la création du compte du partenaire {} : {}",
                    partner.getId(), exception.getMessage());
            throw new IllegalStateException("Erreur lors de la création du compte de paiement Stripe.", exception);
        }
    }

    /*
     * Partenaire : lien vers le formulaire d'inscription hébergé par Stripe.
     * Le lien est à usage unique et expire vite : il est créé à chaque clic,
     * jamais stocké.
     */
    @Transactional(readOnly = true)
    public String createOnboardingLink(String partnerEmail) {
        Partner partner = partnerRepository.findByUserEmail(partnerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Partenaire introuvable."));

        if (partner.getStripeAccountId() == null) {
            throw new IllegalArgumentException(
                    "Votre compte de paiement n'a pas encore été créé par l'administrateur.");
        }

        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(partner.getStripeAccountId())
                .setRefreshUrl(frontendUrl + "/partner?stripe=refresh")
                .setReturnUrl(frontendUrl + "/partner?stripe=return")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        try {
            Stripe.apiKey = stripeSecretKey;
            return AccountLink.create(params).getUrl();
        } catch (StripeException exception) {
            log.error("Stripe a refusé le lien d'inscription du partenaire {} : {}",
                    partner.getId(), exception.getMessage());
            throw new IllegalStateException("Erreur lors de la création du lien d'inscription Stripe.", exception);
        }
    }

    /*
     * État du compte, demandé à Stripe.
     * NOT_CREATED ne coûte aucun appel réseau.
     */
    public AccountStatus getStatus(Partner partner) {
        if (partner.getStripeAccountId() == null) {
            return AccountStatus.NOT_CREATED;
        }

        try {
            Stripe.apiKey = stripeSecretKey;
            Account account = Account.retrieve(partner.getStripeAccountId());

            if (Boolean.TRUE.equals(account.getChargesEnabled())) {
                return AccountStatus.ACTIVE;
            }
            if (Boolean.TRUE.equals(account.getDetailsSubmitted())) {
                return AccountStatus.PENDING_VERIFICATION;
            }
            return AccountStatus.INCOMPLETE;
        } catch (StripeException exception) {
            log.error("Stripe n'a pas renvoyé l'état du compte {} : {}",
                    partner.getStripeAccountId(), exception.getMessage());
            throw new IllegalStateException("Impossible de lire l'état du compte de paiement Stripe.", exception);
        }
    }

    // Règle de vente : un partenaire ne vend que si son compte est actif.
    public boolean canReceivePayments(Partner partner) {
        return getStatus(partner) == AccountStatus.ACTIVE;
    }

    private String contactEmail(Partner partner) {
        if (partner.getEmail() != null && !partner.getEmail().isBlank()) {
            return partner.getEmail();
        }
        return partner.getUser().getEmail();
    }
}