CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_orders_status CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'CANCELLED')),
    CONSTRAINT ck_orders_total_amount_positive CHECK (total_amount > 0),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX ix_orders_user ON orders (user_id);
CREATE INDEX ix_orders_user_status ON orders (user_id, status);

CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name_snapshot VARCHAR(100) NOT NULL,
    price_snapshot DECIMAL(15, 2) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_order_item_product_name_snapshot_not_blank CHECK (CHAR_LENGTH(TRIM(product_name_snapshot)) > 0),
    CONSTRAINT ck_order_item_price_snapshot_positive CHECK (price_snapshot > 0),
    CONSTRAINT ck_order_item_quantity_range CHECK (quantity BETWEEN 1 AND 99),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX ix_order_item_order ON order_item (order_id);
CREATE INDEX ix_order_item_product ON order_item (product_id);
