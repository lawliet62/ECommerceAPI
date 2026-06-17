CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_email_not_blank CHECK (CHAR_LENGTH(TRIM(email)) > 0),
    CONSTRAINT ck_users_password_not_blank CHECK (CHAR_LENGTH(TRIM(password)) > 0),
    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    stock INT NOT NULL,
    active BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_product_name_not_blank CHECK (CHAR_LENGTH(TRIM(name)) > 0),
    CONSTRAINT ck_product_price_positive CHECK (price > 0),
    CONSTRAINT ck_product_stock_non_negative CHECK (stock >= 0)
);

CREATE INDEX ix_product_active ON product (active);
CREATE INDEX ix_product_name ON product (name);
