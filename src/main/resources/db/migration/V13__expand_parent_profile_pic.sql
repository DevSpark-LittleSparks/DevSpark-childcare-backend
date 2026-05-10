-- Expand parent profile picture length for base64 storage
ALTER TABLE parent MODIFY COLUMN profile_picture LONGTEXT;
