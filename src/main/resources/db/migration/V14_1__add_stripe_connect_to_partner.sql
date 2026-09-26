ALTER TABLE partner
    ADD COLUMN stripe_account_id VARCHAR(255) NULL,
    ADD COLUMN commission_rate DECIMAL(4,2) NOT NULL DEFAULT 5.00,
    ADD CONSTRAINT uq_partner_stripe_account_id UNIQUE (stripe_account_id),
    ADD CONSTRAINT chk_partner_commission_rate CHECK (commission_rate BETWEEN 1.00 AND 8.00);