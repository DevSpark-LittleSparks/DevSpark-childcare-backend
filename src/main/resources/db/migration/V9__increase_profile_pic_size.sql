-- Increase size of profile picture columns to support Base64 strings
ALTER TABLE child MODIFY COLUMN profile_pic LONGTEXT;
ALTER TABLE parent MODIFY COLUMN profile_picture LONGTEXT;
