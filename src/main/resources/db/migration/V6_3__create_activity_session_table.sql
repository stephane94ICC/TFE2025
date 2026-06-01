CREATE TABLE activity_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    capacity INT NOT NULL,
    status ENUM('SCHEDULED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'SCHEDULED',
    booking_deadline DATETIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_session_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_activity_session_location
        FOREIGN KEY (location_id) REFERENCES activity_location(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_activity_session_capacity
        CHECK (capacity > 0),

    CONSTRAINT chk_activity_session_dates
        CHECK (end_at > start_at),

    CONSTRAINT chk_activity_session_deadline
        CHECK (booking_deadline <= start_at),

    CONSTRAINT uq_activity_session
        UNIQUE (activity_id, location_id, start_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;