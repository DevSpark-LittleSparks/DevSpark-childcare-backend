DROP TABLE IF EXISTS card_details;

CREATE TABLE card_details (
    card_details_id BINARY(16) NOT NULL PRIMARY KEY,
    card_holder_name VARCHAR(150),
    card_last4 VARCHAR(4),
    card_type VARCHAR(50),
    exp_date DATETIME,
    parent_id BINARY(16),
    CONSTRAINT fk_card_parent FOREIGN KEY (parent_id) REFERENCES parent (parent_id) ON DELETE CASCADE
);
