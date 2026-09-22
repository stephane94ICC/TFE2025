ALTER TABLE product
    ADD COLUMN vat_rate DECIMAL(4,2) NULL;

UPDATE product SET vat_rate = 21.00;

ALTER TABLE product
    MODIFY COLUMN vat_rate DECIMAL(4,2) NOT NULL,
    ADD CONSTRAINT chk_product_vat_rate CHECK (vat_rate IN (0.00, 6.00, 12.00, 21.00));