ALTER TABLE reservation
    ADD COLUMN billing_address_line1 VARCHAR(255) NULL,
    ADD COLUMN billing_address_line2 VARCHAR(255) NULL,
    ADD COLUMN billing_postal_code VARCHAR(20) NULL,
    ADD COLUMN billing_city VARCHAR(100) NULL,
    ADD COLUMN billing_country CHAR(2) NULL;