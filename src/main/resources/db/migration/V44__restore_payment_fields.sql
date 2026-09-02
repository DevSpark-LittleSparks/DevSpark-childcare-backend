-- ================================================================
-- Purpose : V33__recreate_payment_schema.sql (DROP + CREATE) undid the
--           columns that V22 and V23 had added to payment_transaction
--           and payment earlier, since V33 runs after both and recreated
--           the tables without them. Restore both columns so the
--           PaymentTransaction/Payment entities match the schema again.
-- ================================================================

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_name = 'payment_transaction' AND table_schema = DATABASE() AND column_name = 'requested_at'
  ) > 0,
  "SELECT 1",
  "ALTER TABLE payment_transaction ADD COLUMN requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;"
));
PREPARE addColumn1 FROM @preparedStatement;
EXECUTE addColumn1;
DEALLOCATE PREPARE addColumn1;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_name = 'payment' AND table_schema = DATABASE() AND column_name = 'description'
  ) > 0,
  "SELECT 1",
  "ALTER TABLE payment ADD COLUMN description VARCHAR(255) NULL AFTER billing_month;"
));
PREPARE addColumn2 FROM @preparedStatement;
EXECUTE addColumn2;
DEALLOCATE PREPARE addColumn2;
