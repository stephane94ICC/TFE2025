

ALTER TABLE orders
    ADD COLUMN billing_first_name VARCHAR(100) NULL,
    ADD COLUMN billing_last_name  VARCHAR(100) NULL,
    ADD COLUMN billing_email      VARCHAR(255) NULL;

UPDATE orders o
    JOIN users u ON u.id = o.user_id
SET o.billing_first_name = u.first_name,
    o.billing_last_name  = u.last_name,
    o.billing_email      = u.email;

ALTER TABLE orders
    MODIFY COLUMN billing_first_name VARCHAR(100) NOT NULL,
    MODIFY COLUMN billing_last_name  VARCHAR(100) NOT NULL,
    MODIFY COLUMN billing_email      VARCHAR(255) NOT NULL;