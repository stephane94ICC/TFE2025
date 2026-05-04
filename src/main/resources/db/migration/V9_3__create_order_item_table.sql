CREATE TABLE order_item (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            unit_price DECIMAL(10,2) NOT NULL,
                            PRIMARY KEY (id),

                            CONSTRAINT fk_order_item_order
                                FOREIGN KEY (order_id) REFERENCES orders(id),

                            CONSTRAINT fk_order_item_product
                                FOREIGN KEY (product_id) REFERENCES product(id)
);