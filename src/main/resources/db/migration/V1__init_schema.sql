CREATE TABLE users (
    id          CHAR(36)        NOT NULL            COMMENT 'UUID',
    email       VARCHAR(255)    NOT NULL,
    password    VARCHAR(255)    NOT NULL             COMMENT 'BCrypt hashed',
    full_name   VARCHAR(100),
    phone       VARCHAR(20),
    avatar_url  VARCHAR(500)    NOT NULL
                DEFAULT 'https://example.com/default-avatar.png',
    role        ENUM('USER','ADMIN')
                NOT NULL DEFAULT 'USER',
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB COMMENT='System users';

CREATE TABLE categories (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)    NOT NULL,
    classify    ENUM('FOOD','DRINK') NOT NULL,
    description TEXT,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='Product categories';

CREATE TABLE products (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    name         VARCHAR(200)    NOT NULL,
    description  TEXT,
    price        DECIMAL(10,2)   NOT NULL CHECK (price >= 0),
    image_url    VARCHAR(500)    NOT NULL
                 DEFAULT 'https://example.com/default-product.png',
    stock        INT             NOT NULL DEFAULT 0 CHECK (stock >= 0),
    classify     ENUM('FOOD','DRINK') NOT NULL,
    category_id  BIGINT          NOT NULL,
    avg_rating   DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    rating_count INT             NOT NULL DEFAULT 0,
    is_available BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                 ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='Products (food & drink)';

CREATE TABLE cart_items (
    id          BIGINT    NOT NULL AUTO_INCREMENT,
    user_id     CHAR(36)  NOT NULL,
    product_id  BIGINT    NOT NULL,
    quantity    INT       NOT NULL DEFAULT 1 CHECK (quantity > 0),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_cart_user_product (user_id, product_id),
    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id)    REFERENCES users    (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Shopping cart items';

CREATE TABLE orders (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    user_id          CHAR(36)      NOT NULL,
    status           ENUM('PENDING','CONFIRMED','PREPARING',
                          'DELIVERED','DONE','CANCELLED')
                     NOT NULL DEFAULT 'PENDING',
    total_amount     DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    note             TEXT,
    delivery_address VARCHAR(500),
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                     ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='Customer orders';

CREATE TABLE order_items (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    order_id    BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    quantity    INT           NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10,2) NOT NULL COMMENT 'Price snapshot at the time of order',
    subtotal    DECIMAL(10,2) NOT NULL COMMENT 'quantity * unit_price',

    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)   REFERENCES orders   (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='Order line items';

CREATE TABLE ratings (
    id          BIGINT    NOT NULL AUTO_INCREMENT,
    user_id     CHAR(36)  NOT NULL,
    product_id  BIGINT    NOT NULL,
    order_id    BIGINT    NOT NULL COMMENT 'Verify that the user has purchased the product',
    score       TINYINT   NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_rating_user_product (user_id, product_id),
    CONSTRAINT fk_ratings_user
        FOREIGN KEY (user_id)    REFERENCES users    (id) ON DELETE CASCADE,
    CONSTRAINT fk_ratings_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_ratings_order
        FOREIGN KEY (order_id)   REFERENCES orders   (id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='Product ratings';

CREATE TABLE suggestions (
    id          BIGINT    NOT NULL AUTO_INCREMENT,
    user_id     CHAR(36)  NOT NULL,
    name        VARCHAR(200) NOT NULL,
    classify    ENUM('FOOD','DRINK'),
    description TEXT,
    status      ENUM('PENDING','APPROVED','REJECTED')
                NOT NULL DEFAULT 'PENDING',
    admin_note  TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_suggestions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Product suggestions from users';




CREATE INDEX idx_products_classify   ON products (classify);
CREATE INDEX idx_products_category   ON products (category_id);
CREATE INDEX idx_products_price      ON products (price);
CREATE INDEX idx_products_avg_rating ON products (avg_rating);
CREATE INDEX idx_products_available  ON products (is_available);

CREATE INDEX idx_orders_user_id      ON orders (user_id);
CREATE INDEX idx_orders_status       ON orders (status);
CREATE INDEX idx_orders_created_at   ON orders (created_at);

CREATE INDEX idx_suggestions_status  ON suggestions (status);
