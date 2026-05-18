CREATE TABLE activity_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    activity_id BIGINT NOT NULL,
    CONSTRAINT fk_activity_images_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;