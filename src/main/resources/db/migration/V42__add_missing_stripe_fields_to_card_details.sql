SET @dbname = DATABASE();
SET @tablename = 'card_details';

-- 1. Add stripe_payment_method_id
SET @columnname1 = 'stripe_payment_method_id';
SET @preparedStatement1 = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname1)
  ) > 0,
  "SELECT 1",
  "ALTER TABLE card_details ADD COLUMN stripe_payment_method_id VARCHAR(255) NULL UNIQUE;"
));
PREPARE addColumn1 FROM @preparedStatement1;
EXECUTE addColumn1;
DEALLOCATE PREPARE addColumn1;

-- 2. Add paid_via
SET @columnname2 = 'paid_via';
SET @preparedStatement2 = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname2)
  ) > 0,
  "SELECT 1",
  "ALTER TABLE card_details ADD COLUMN paid_via VARCHAR(50) NULL;"
));
PREPARE addColumn2 FROM @preparedStatement2;
EXECUTE addColumn2;
DEALLOCATE PREPARE addColumn2;
