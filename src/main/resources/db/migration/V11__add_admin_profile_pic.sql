-- Add profile picture and other info to admin table
ALTER TABLE admin ADD COLUMN profile_pic LONGTEXT;
ALTER TABLE admin ADD COLUMN phone1 VARCHAR(20);
ALTER TABLE admin ADD COLUMN phone2 VARCHAR(20);
ALTER TABLE admin ADD COLUMN address TEXT;
ALTER TABLE admin ADD COLUMN center_name VARCHAR(150);
ALTER TABLE admin ADD COLUMN capacity VARCHAR(50);
