-- Migration to add guardian_name to child table
ALTER TABLE child ADD COLUMN guardian_name VARCHAR(150) NULL;
