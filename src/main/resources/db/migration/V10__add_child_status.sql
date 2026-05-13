-- V10: Add status to child table for lifecycle management
ALTER TABLE child ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED';
