ALTER TABLE reservation
    ADD COLUMN stripe_refund_id VARCHAR(255) NULL,
    ADD COLUMN refunded_at DATETIME NULL,
    ADD CONSTRAINT uk_reservation_stripe_refund_id UNIQUE (stripe_refund_id),
    ADD CONSTRAINT chk_reservation_refund_all_or_none CHECK (
        (stripe_refund_id IS NULL AND refunded_at IS NULL)
        OR (stripe_refund_id IS NOT NULL AND refunded_at IS NOT NULL)
    ),
    ADD CONSTRAINT chk_reservation_refund_only_cancelled CHECK (
        stripe_refund_id IS NULL OR status = 'CANCELLED'
    );