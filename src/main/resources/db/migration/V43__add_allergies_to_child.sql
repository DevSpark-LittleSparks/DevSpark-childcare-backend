-- Add allergies column to child table (to support Teacher UI Allergy Alerts)
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_name = 'child' AND table_schema = DATABASE() AND column_name = 'allergies'
  ) > 0,
  "SELECT 1",
  "ALTER TABLE child ADD COLUMN allergies VARCHAR(255) DEFAULT NULL;"
));
PREPARE addColumn FROM @preparedStatement;
EXECUTE addColumn;
DEALLOCATE PREPARE addColumn;