-- ================================================================
--  DevSpark — Child Care Management System
--  File    : V8__convert_remaining_request_ids_to_binary.sql
--  Purpose : Convert request_id columns in director_registration_request
--            and teacher_registration_request from CHAR(36) to BINARY(16).
--            V7 missed these two tables; this migration completes the
--            UUID → BINARY(16) alignment required by Hibernate 6.x.
-- ================================================================

ALTER TABLE director_registration_request
    MODIFY request_id BINARY(16) NOT NULL;

ALTER TABLE teacher_registration_request
    MODIFY request_id BINARY(16) NOT NULL;
