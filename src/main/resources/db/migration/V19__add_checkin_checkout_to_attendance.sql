-- Add the check_in and check_out columns missing from the V1 file
ALTER TABLE attendance
    ADD COLUMN check_in TIME NULL,
ADD COLUMN check_out TIME NULL;

-- Update the status enum to include HALF_DAY (excluding SICK)
ALTER TABLE attendance
    MODIFY COLUMN status ENUM('PRESENT','ABSENT','HALF_DAY') NOT NULL;