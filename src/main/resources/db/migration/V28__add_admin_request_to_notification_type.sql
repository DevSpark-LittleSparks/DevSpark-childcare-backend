-- ================================================================
-- DevSpark — Child Care Management System
-- File    : V28__add_admin_request_to_notification_type.sql
-- Description: Expand notification type enum for admin requests
-- ================================================================

ALTER TABLE notification MODIFY COLUMN type ENUM('BROADCAST', 'SYSTEM', 'ADMIN_REQUEST') DEFAULT 'SYSTEM';
