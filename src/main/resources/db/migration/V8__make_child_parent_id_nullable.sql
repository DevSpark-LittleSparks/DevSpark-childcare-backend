-- Migration to allow null parent_id during initial child admission
ALTER TABLE child MODIFY parent_id CHAR(36) NULL;
