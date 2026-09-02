-- ================================================================
-- File    : V18__add_payment_transaction_requested_at.sql
-- Purpose : Track when a charge was requested from Stripe, separate from
--           tnx_time (when it actually resolved successful/unsuccessful).
-- ================================================================

ALTER TABLE payment_transaction ADD COLUMN IF NOT EXISTS requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
