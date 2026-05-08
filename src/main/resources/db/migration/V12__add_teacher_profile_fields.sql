-- Add missing profile fields to teacher table
ALTER TABLE teacher ADD COLUMN phone VARCHAR(20);
ALTER TABLE teacher ADD COLUMN address TEXT;
ALTER TABLE teacher MODIFY COLUMN profile_picture LONGTEXT;
