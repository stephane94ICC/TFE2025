CREATE TABLE activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    duration_minutes INT,
    partner_id BIGINT NOT NULL,
    CONSTRAINT fk_activity_partner
        FOREIGN KEY (partner_id) REFERENCES partner(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
