CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES product(id)
        ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;