ALTER TABLE order_item
    ADD COLUMN vat_rate DECIMAL(4,2) NULL,
    ADD CONSTRAINT chk_order_item_vat_rate CHECK (vat_rate IN (0.00, 6.00, 12.00, 21.00));