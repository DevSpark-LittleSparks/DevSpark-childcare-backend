-- Add missing columns to director_registration_request to match AuditableEntity
ALTER TABLE director_registration_request ADD COLUMN deleted_at TIMESTAMP NULL;

-- Also check if parent_registration_request needs child_name if we are mapping childFirstName to it
-- But for now let's just fix the missing audit columns
