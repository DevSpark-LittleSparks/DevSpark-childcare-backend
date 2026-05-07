-- Update profile picture columns to LONGTEXT to support base64 images
ALTER TABLE child MODIFY profile_pic LONGTEXT;
ALTER TABLE parent MODIFY profile_picture LONGTEXT;
ALTER TABLE teacher MODIFY profile_picture LONGTEXT;
ALTER TABLE admin_profile MODIFY profile_image_url LONGTEXT;
