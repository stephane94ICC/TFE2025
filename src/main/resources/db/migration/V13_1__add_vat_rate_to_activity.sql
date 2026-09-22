ALTER TABLE activity
    ADD COLUMN vat_rate DECIMAL(4,2) NULL;

UPDATE activity SET vat_rate = 21.00;

ALTER TABLE activity
    MODIFY COLUMN vat_rate DECIMAL(4,2) NOT NULL,
    ADD CONSTRAINT chk_activity_vat_rate CHECK (vat_rate IN (0.00, 6.00, 12.00, 21.00));