SET @dbname = DATABASE();
SET @tablename = 'activity';
SET @columnname = 'category';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  "SELECT 1",
  "ALTER TABLE activity ADD COLUMN category VARCHAR(100) NOT NULL DEFAULT 'OTHER';"
));
PREPARE addColumnIfNotExist FROM @preparedStatement;
EXECUTE addColumnIfNotExist;
DEALLOCATE PREPARE addColumnIfNotExist;
