-- ================================================================
-- File    : V19__add_payment_description.sql
-- Purpose : What a charge is for - null for regular monthly billing, set
--           for one-off additional charges (registration fee, facility
--           fee, etc).
-- ================================================================

ALTER TABLE payment ADD COLUMN IF NOT EXISTS description VARCHAR(255) NULL AFTER billing_month;
