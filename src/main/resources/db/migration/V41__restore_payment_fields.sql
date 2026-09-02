-- ================================================================
-- Purpose : V30__recreate_payment_schema.sql (DROP + CREATE) undid the
--           columns that V22 and V23 had added to payment_transaction
--           and payment earlier, since V30 runs after both and recreated
--           the tables without them. Restore both columns so the
--           PaymentTransaction/Payment entities match the schema again.
-- ================================================================

ALTER TABLE payment_transaction ADD COLUMN requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE payment ADD COLUMN description VARCHAR(255) NULL AFTER billing_month;
