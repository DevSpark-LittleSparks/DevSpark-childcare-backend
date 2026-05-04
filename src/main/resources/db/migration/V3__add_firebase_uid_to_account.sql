-- ================================================================
--  DevSpark — Child Care Management System
--  File    : V3__add_firebase_uid_to_account.sql
--  Description: Add firebase_uid to account table
-- ================================================================

ALTER TABLE account ADD COLUMN firebase_uid VARCHAR(128) UNIQUE AFTER account_id;
