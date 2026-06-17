CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT NOT NULL,
    order_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    pending_order_id BIGINT GENERATED ALWAYS AS (
        IF(status = 'PENDING', order_id, NULL)
    ) STORED INVISIBLE,
    PRIMARY KEY (id),
    CONSTRAINT ck_payment_status CHECK (status IN ('PENDING', 'SUCCESS', 'CANCELLED', 'FAILED')),
    CONSTRAINT ck_payment_amount_positive CHECK (amount > 0),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uk_payment_pending_order UNIQUE (pending_order_id)
);

CREATE INDEX ix_payment_order ON payment (order_id);
CREATE INDEX ix_payment_order_status ON payment (order_id, status);
