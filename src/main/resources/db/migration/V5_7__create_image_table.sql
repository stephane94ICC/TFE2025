CREATE TABLE image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    activity_id BIGINT NOT NULL,
    CONSTRAINT fk_image_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
