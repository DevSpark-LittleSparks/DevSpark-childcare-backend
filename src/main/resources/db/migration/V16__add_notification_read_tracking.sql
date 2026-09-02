-- ================================================================
--  File    : V16__add_notification_read_tracking.sql
--  Purpose : Track which users have read which notifications
-- ================================================================

CREATE TABLE notification_read (
    read_id         BINARY(16)  NOT NULL,
    account_id      BINARY(16)  NOT NULL,
    notification_id BINARY(16)  NOT NULL,
    read_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (read_id),
    UNIQUE KEY uk_notification_user_read (account_id, notification_id),
    CONSTRAINT fk_read_account FOREIGN KEY (account_id) REFERENCES account (account_id),
    CONSTRAINT fk_read_notification FOREIGN KEY (notification_id) REFERENCES notification (notification_id)
);

CREATE INDEX idx_notification_read_account ON notification_read (account_id);
