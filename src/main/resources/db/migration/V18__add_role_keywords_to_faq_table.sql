-- ================================================================
--  File    : V17__add_role_keywords_to_faq_table.sql
--  Purpose : Extend the existing (unused) faq table so it can back
--            the Sprouty chatbot's keyword-matched FAQ lookup,
--            scoped per role.
-- ================================================================

ALTER TABLE faq
    ADD COLUMN role     VARCHAR(20)  NULL     AFTER faq_id,   -- NULL = applies to all roles
    ADD COLUMN keywords VARCHAR(500) NOT NULL AFTER role;      -- comma-separated match keywords

CREATE INDEX idx_faq_role ON faq (role);
