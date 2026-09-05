
ALTER TABLE reservation
    ADD COLUMN billing_first_name VARCHAR(100) NULL,
    ADD COLUMN billing_last_name  VARCHAR(100) NULL,
    ADD COLUMN billing_email      VARCHAR(255) NULL;

UPDATE reservation r
    JOIN users u ON u.id = r.user_id
SET r.billing_first_name = u.first_name,
    r.billing_last_name  = u.last_name,
    r.billing_email      = u.email;

ALTER TABLE reservation
    MODIFY COLUMN billing_first_name VARCHAR(100) NOT NULL,
    MODIFY COLUMN billing_last_name  VARCHAR(100) NOT NULL,
    MODIFY COLUMN billing_email      VARCHAR(255) NOT NULL;