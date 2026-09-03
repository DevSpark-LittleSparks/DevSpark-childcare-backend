-- ================================================================
--  File    : V39__restore_payment_columns_dropped_by_recreates.sql
--  Purpose : V29 and V30 rebuild card_details, payment and
--            payment_transaction with DROP TABLE + CREATE TABLE, which
--            silently discarded the columns V21, V22 and V23 had already
--            added. Those ALTERs are numbered below the recreates, so the
--            loss happens on every database including a fresh one, and
--            leaves ddl-auto=validate failing at startup on columns the
--            entities still map.
--
--            Plain ADD COLUMN is used deliberately rather than the
--            ADD COLUMN IF NOT EXISTS spelling of V21-V23: that is a
--            MariaDB extension MySQL 8 rejects, and the recreates already
--            guarantee these columns are absent.
-- ================================================================

-- CardDetails.stripePaymentMethodId, CardDetails.paidVia (originally V21)
ALTER TABLE card_details
    ADD COLUMN stripe_payment_method_id VARCHAR(255) NULL UNIQUE,
    ADD COLUMN paid_via                 VARCHAR(50)  NULL;

-- Payment.description (originally V23)
ALTER TABLE payment
    ADD COLUMN description VARCHAR(255) NULL AFTER billing_month;

-- PaymentTransaction.requestedAt (originally V22)
ALTER TABLE payment_transaction
    ADD COLUMN requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
