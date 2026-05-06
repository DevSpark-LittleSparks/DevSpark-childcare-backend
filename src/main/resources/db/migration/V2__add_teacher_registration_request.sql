-- ================================================================
--  DevSpark — Child Care Management System
--  File    : V2__add_teacher_registration_request.sql
--  Team    : DevSpark
--  Description: Table for teacher registration requests
-- ================================================================

CREATE TABLE teacher_registration_request (
    request_id      CHAR(36)        NOT NULL DEFAULT (UUID()),
    full_name       VARCHAR(150)    NOT NULL,
    email           VARCHAR(150)    NOT NULL,
    phone           VARCHAR(20),
    address         TEXT,
    designation     ENUM('SENIOR','JUNIOR') NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    status          ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',

    -- Audit Columns
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(128),
    
    -- Soft delete
    deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_at      DATETIME        NULL,

    PRIMARY KEY (request_id),
    UNIQUE KEY uk_teacher_request_email (email)
);

CREATE INDEX idx_teacher_request_status ON teacher_registration_request (status);
