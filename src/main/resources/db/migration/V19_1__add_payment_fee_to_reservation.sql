ALTER TABLE reservation
    ADD COLUMN payment_fee_htva DECIMAL(10,2) NULL,
    ADD COLUMN payment_fee_vat DECIMAL(10,2) NULL,
    ADD CONSTRAINT chk_reservation_payment_fee_amounts CHECK (payment_fee_htva >= 0 AND payment_fee_vat >= 0),
    ADD CONSTRAINT chk_reservation_payment_fee_both_or_none CHECK (
        (payment_fee_htva IS NULL AND payment_fee_vat IS NULL)
            OR (payment_fee_htva IS NOT NULL AND payment_fee_vat IS NOT NULL)
        );