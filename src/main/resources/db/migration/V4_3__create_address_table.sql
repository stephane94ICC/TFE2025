CREATE TABLE address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    partner_id BIGINT NOT NULL,
    CONSTRAINT fk_address_partner
        FOREIGN KEY (partner_id) REFERENCES partner(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
