-- ================================================================
--  File    : V41__add_payment_created_at.sql
--  Purpose : Payment carried no timestamp of any kind, so "Recent
--            Invoices" had nothing to sort by - the frontend was
--            synthesising a date from billing_month, which made every
--            row in a given month read as the 1st, and the repository
--            methods returned rows in whatever order the database chose.
--            A newly added charge could therefore land anywhere in the
--            list, and fall outside a truncated "recent" view entirely.
--
--  Note    : Existing rows are all backfilled to the migration timestamp,
--            so they tie with each other and their relative order stays
--            arbitrary. Only payments created from here on sort correctly.
-- ================================================================

ALTER TABLE payment
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_payment_parent_created ON payment (parent_id, created_at);
