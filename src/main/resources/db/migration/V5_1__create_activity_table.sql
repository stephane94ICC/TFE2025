CREATE TABLE activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    duration_minutes INT NOT NULL,
    minimum_age INT NOT NULL DEFAULT 0,
    equipment_information TEXT,

    status ENUM(
        'PENDING_REVIEW',
        'APPROVED',
        'REJECTED',
        'DISABLED'
    ) NOT NULL DEFAULT 'PENDING_REVIEW',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    review_comment TEXT,
    reviewed_by_user_id BIGINT NULL,

    partner_id BIGINT NOT NULL,

    CONSTRAINT chk_activity_price
        CHECK (price >= 0),

    CONSTRAINT chk_activity_duration
        CHECK (duration_minutes > 0),

    CONSTRAINT chk_activity_minimum_age
        CHECK (minimum_age >= 0),

    CONSTRAINT fk_activity_partner
        FOREIGN KEY (partner_id) REFERENCES partner(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_activity_reviewed_by_user
        FOREIGN KEY (reviewed_by_user_id) REFERENCES users(id)
        ON DELETE SET NULL,

    INDEX idx_activity_status (status),
    INDEX idx_activity_partner_status (partner_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
