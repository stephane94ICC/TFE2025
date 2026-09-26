ALTER TABLE reservation
    ADD COLUMN commission_rate DECIMAL(4,2) NULL,
    ADD COLUMN commission_htva DECIMAL(10,2) NULL,
    ADD COLUMN commission_vat DECIMAL(10,2) NULL,
    ADD CONSTRAINT chk_reservation_commission_rate CHECK (commission_rate BETWEEN 1.00 AND 8.00),
    ADD CONSTRAINT chk_reservation_commission_amounts CHECK (commission_htva >= 0 AND commission_vat >= 0),
    ADD CONSTRAINT chk_reservation_commission_all_or_none CHECK (
        (commission_rate IS NULL AND commission_htva IS NULL AND commission_vat IS NULL)
        OR (commission_rate IS NOT NULL AND commission_htva IS NOT NULL AND commission_vat IS NOT NULL)
    );