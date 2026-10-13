package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.dto.reservation.PartnerReservationResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivitySession;
import be.loisirs.tfe2025.plateforme_loisirs.entity.DocumentType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Reservation;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ReservationStatus;
import be.loisirs.tfe2025.plateforme_loisirs.repository.PartnerDocumentRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Ventes du partenaire connecté (page « Mes ventes »).
 */
@Service
public class PartnerReservationService {

    private final ReservationRepository reservationRepository;
    private final PartnerDocumentRepository documentRepository;

    public PartnerReservationService(ReservationRepository reservationRepository,
                                     PartnerDocumentRepository documentRepository) {
        this.reservationRepository = reservationRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional(readOnly = true)
    public List<PartnerReservationResponseDTO> getSales(String partnerEmail) {
        List<Reservation> sales = reservationRepository
                .findPartnerSales(partnerEmail, ReservationStatus.CONFIRMED, DocumentType.INVOICE);

        if (sales.isEmpty()) {
            return List.of();
        }

        // Deux requêtes pour toute la liste, et aucun PDF chargé : seulement les identifiants.
        List<Long> ids = sales.stream().map(Reservation::getId).toList();
        Set<Long> invoiced = documentRepository.findReservationIdsWithDocument(ids, DocumentType.INVOICE);
        Set<Long> credited = documentRepository.findReservationIdsWithDocument(ids, DocumentType.CREDIT_NOTE);

        return sales.stream()
                .map(sale -> toDTO(sale, invoiced.contains(sale.getId()), credited.contains(sale.getId())))
                .toList();
    }

    /**
     * Un document de vente n'est possible que pour une vente payée en ligne, dont la TVA
     * et la date de paiement sont figées. Les réservations du jeu de démonstration,
     * antérieures à ces colonnes, n'en ont pas : aucun document chiffré sans taux figé.
     */
    public static boolean isReceiptAvailable(Reservation reservation) {
        return reservation.getStatus() == ReservationStatus.CONFIRMED
                && reservation.getVatRate() != null
                && reservation.getConfirmedAt() != null;
    }

    /**
     * Une facture exige en plus l'adresse du client, figée au paiement par le webhook Stripe
     * (le code postal peut manquer dans certains pays). Sans elle : justificatif seulement.
     */
    public static boolean isInvoiceAvailable(Reservation reservation) {
        return isReceiptAvailable(reservation)
                && reservation.getBillingAddressLine1() != null
                && reservation.getBillingCity() != null
                && reservation.getBillingCountry() != null;
    }

    /** Vente annulée ET remboursée (MemberReservationService) : sa facture doit être annulée. */
    public static boolean isRefunded(Reservation reservation) {
        return reservation.getStatus() == ReservationStatus.CANCELLED
                && reservation.getRefundedAt() != null;
    }

    private PartnerReservationResponseDTO toDTO(Reservation reservation, boolean invoiced, boolean credited) {
        ActivitySession session = reservation.getSession();

        PartnerReservationResponseDTO dto = new PartnerReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setReference(reservation.getReference());
        dto.setActivityTitle(session.getActivity().getTitle());
        dto.setSessionStartAt(session.getStartAt());
        dto.setCustomerName(reservation.getBillingFirstName() + " " + reservation.getBillingLastName());
        dto.setQuantity(reservation.getQuantity());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setPaidAt(reservation.getConfirmedAt());
        dto.setStatus(reservation.getStatus().name());

        // Une facture émise reste téléchargeable, même après le remboursement.
        if (invoiced || isInvoiceAvailable(reservation)) {
            dto.setDocumentType(PartnerReservationResponseDTO.INVOICE);
        } else if (isReceiptAvailable(reservation)) {
            dto.setDocumentType(PartnerReservationResponseDTO.RECEIPT);
        }

        dto.setCreditNoteAvailable(credited || (invoiced && isRefunded(reservation)));

        return dto;
    }
}