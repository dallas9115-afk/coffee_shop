CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menu (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    price      INT          NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS point (
    id         BIGINT   AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT   NOT NULL,
    balance    BIGINT   NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_point_user_id UNIQUE (user_id),
    CONSTRAINT fk_point_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS point_history (
    id            BIGINT      AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    type          VARCHAR(20) NOT NULL,
    amount        BIGINT      NOT NULL,
    balance_after BIGINT      NOT NULL,
    created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_point_history_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_point_history_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS orders (
    id         BIGINT      AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    menu_id    BIGINT      NOT NULL,
    price      INT         NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    ordered_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_order_menu FOREIGN KEY (menu_id) REFERENCES menu (id),
    INDEX idx_order_status_ordered_at_menu_id (status, ordered_at, menu_id)
);

CREATE TABLE IF NOT EXISTS outbox_event (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    aggregate_type VARCHAR(50)  NOT NULL,
    aggregate_id   BIGINT       NOT NULL,
    payload        TEXT         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at        DATETIME     NULL,

    INDEX idx_outbox_status_created_at (status, created_at)
);
