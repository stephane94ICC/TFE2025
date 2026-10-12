ALTER TABLE partner_document
    ADD COLUMN document_type VARCHAR(20) NOT NULL AFTER reservation_id,
    ADD COLUMN credited_document_id BIGINT NULL AFTER document_type,
    ADD COLUMN content MEDIUMBLOB NOT NULL,

    ADD CONSTRAINT uk_partner_document_reservation_type
        UNIQUE (reservation_id, document_type),

    ADD CONSTRAINT fk_partner_document_credited
        FOREIGN KEY (credited_document_id) REFERENCES partner_document(id),

    ADD CONSTRAINT chk_partner_document_type
        CHECK (document_type IN ('INVOICE', 'CREDIT_NOTE')),

    ADD CONSTRAINT chk_partner_document_credited
        CHECK (
            (document_type = 'INVOICE' AND credited_document_id IS NULL)
            OR (document_type = 'CREDIT_NOTE' AND credited_document_id IS NOT NULL)
        );

ALTER TABLE partner_document
    DROP INDEX uk_partner_document_reservation;