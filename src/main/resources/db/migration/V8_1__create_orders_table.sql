CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'CANCELLED', 'SHIPPED')
        NOT NULL DEFAULT 'PENDING',

    stripe_session_id VARCHAR(255) NULL UNIQUE,
    stripe_payment_intent_id VARCHAR(255) NULL,
    paid_at TIMESTAMP NULL,

    user_id BIGINT NOT NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_orders_total_amount
        CHECK (total_amount >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;