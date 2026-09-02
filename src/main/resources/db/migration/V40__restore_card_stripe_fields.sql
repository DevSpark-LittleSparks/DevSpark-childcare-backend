-- ================================================================
-- Purpose : V29__recreate_card_details_table.sql (DROP + CREATE) undid
--           the stripe_payment_method_id / paid_via columns that
--           V21__add_card_stripe_fields.sql had added earlier, since
--           V29 runs after V21 and recreated the table without them.
--           Restore both columns so the CardDetails entity matches
--           the actual schema again.
-- ================================================================

ALTER TABLE card_details ADD COLUMN stripe_payment_method_id VARCHAR(255) NULL UNIQUE;
ALTER TABLE card_details ADD COLUMN paid_via VARCHAR(50) NULL;
