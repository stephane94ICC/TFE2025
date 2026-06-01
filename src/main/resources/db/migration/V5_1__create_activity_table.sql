CREATE TABLE activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2),
    duration_minutes INT,
    minimum_age INT NOT NULL DEFAULT 0,
    equipment_information TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    partner_id BIGINT NOT NULL,
    CONSTRAINT fk_activity_partner
        FOREIGN KEY (partner_id) REFERENCES partner(id)
        ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;