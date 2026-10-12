package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.DocumentType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.PartnerDocument;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;


public interface PartnerDocumentRepository extends Repository<PartnerDocument, Long> {

    PartnerDocument save(PartnerDocument document);

    /** La facture ou la note de crédit d'une vente (au plus une de chaque, UNIQUE V18_2). */
    Optional<PartnerDocument> findByReservation_IdAndDocumentType(Long reservationId, DocumentType documentType);


    @Query("SELECT d.reservation.id FROM PartnerDocument d " +
            "WHERE d.documentType = :documentType AND d.reservation.id IN :reservationIds")
    Set<Long> findReservationIdsWithDocument(@Param("reservationIds") Collection<Long> reservationIds,
                                             @Param("documentType") DocumentType documentType);


    @Query("SELECT COALESCE(MAX(d.sequenceNumber), 0) FROM PartnerDocument d " +
            "WHERE d.partner.id = :partnerId AND d.issueYear = :issueYear")
    int findLastSequenceNumber(@Param("partnerId") Long partnerId,
                               @Param("issueYear") int issueYear);
}