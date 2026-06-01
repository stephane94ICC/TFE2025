CREATE TABLE reservation_hold (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,

    CONSTRAINT fk_reservation_hold_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservation_hold_session
        FOREIGN KEY (session_id) REFERENCES activity_session(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_reservation_hold_quantity
        CHECK (quantity > 0),

    INDEX idx_reservation_hold_session_expiration (session_id, expires_at),
    INDEX idx_reservation_hold_user (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;