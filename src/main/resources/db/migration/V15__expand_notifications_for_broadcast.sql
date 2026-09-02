-- ================================================================
-- DevSpark — Child Care Management System
-- File    : V15__expand_notifications_for_broadcast.sql
-- Description: Expand notification table for broadcast support
-- ================================================================

ALTER TABLE notification ADD COLUMN title VARCHAR(200);
ALTER TABLE notification ADD COLUMN priority ENUM('HIGH', 'NORMAL') DEFAULT 'NORMAL';
ALTER TABLE notification ADD COLUMN type ENUM('BROADCAST', 'SYSTEM') DEFAULT 'SYSTEM';
