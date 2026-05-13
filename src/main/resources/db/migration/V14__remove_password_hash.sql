-- ================================================================
-- DevSpark — Child Care Management System
-- File    : V14__remove_password_hash.sql
-- Description: Drop redundant password_hash columns as the system migrates to Firebase-only authentication.
-- ================================================================

ALTER TABLE account DROP COLUMN password_hash;
ALTER TABLE teacher_registration_request DROP COLUMN password_hash;
ALTER TABLE parent_registration_request DROP COLUMN password_hash;
ALTER TABLE director_registration_request DROP COLUMN password_hash;
