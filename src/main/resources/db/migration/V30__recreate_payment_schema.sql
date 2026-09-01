SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS payment_transaction;

CREATE TABLE payment_transaction (
    txn_id BINARY(16) NOT NULL PRIMARY KEY,
    tnx_time DATETIME,
    status VARCHAR(50),
    gateway_reference VARCHAR(255)
);

CREATE TABLE payment (
    payment_id BINARY(16) NOT NULL PRIMARY KEY,
    billing_month VARCHAR(20),
    amount BIGINT,
    status VARCHAR(50),
    parent_id BINARY(16),
    transaction_txn_id BINARY(16),
    CONSTRAINT fk_payment_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_txn FOREIGN KEY (transaction_txn_id) REFERENCES payment_transaction (txn_id) ON DELETE SET NULL
);

SET FOREIGN_KEY_CHECKS = 1;
