CREATE TABLE partner_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partner_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    issue_year INT NOT NULL,
    sequence_number INT NOT NULL,
    document_number VARCHAR(30) NOT NULL,
    issued_at DATETIME NOT NULL,

    CONSTRAINT uk_partner_document_reservation
        UNIQUE (reservation_id),

    CONSTRAINT uk_partner_document_number
        UNIQUE (document_number),

    CONSTRAINT uk_partner_document_sequence
        UNIQUE (partner_id, issue_year, sequence_number),

    CONSTRAINT fk_partner_document_partner
        FOREIGN KEY (partner_id) REFERENCES partner(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_partner_document_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservation(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_partner_document_sequence_number
        CHECK (sequence_number > 0),

    CONSTRAINT chk_partner_document_issue_year
        CHECK (issue_year >= 2025)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;