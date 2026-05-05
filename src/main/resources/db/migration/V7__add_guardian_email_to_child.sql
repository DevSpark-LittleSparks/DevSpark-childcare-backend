-- V7: Add guardian_email column to child table
-- This column stores the pre-registered parent/guardian email address
-- entered by admin during child admissions. It is used to validate
-- parent signup requests — only emails matching this column are allowed
-- to submit a parent signup request.

ALTER TABLE child
    ADD COLUMN guardian_email VARCHAR(150) NOT NULL DEFAULT '' AFTER parent_id;

-- Add index for fast lookup during parent signup validation
CREATE INDEX idx_child_guardian_email ON child (guardian_email);
