CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'en attente',
    user_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reservation_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
