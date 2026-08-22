-- ================================================================
-- File    : V17__add_card_stripe_fields.sql
-- Purpose : Store the tokenized Stripe payment method id on saved cards -
--           the raw card number is never persisted, only what Stripe.js
--           hands back after client-side tokenization.
-- ================================================================

ALTER TABLE card_details ADD COLUMN IF NOT EXISTS stripe_payment_method_id VARCHAR(255) NULL UNIQUE;
ALTER TABLE card_details ADD COLUMN IF NOT EXISTS paid_via VARCHAR(50) NULL;
