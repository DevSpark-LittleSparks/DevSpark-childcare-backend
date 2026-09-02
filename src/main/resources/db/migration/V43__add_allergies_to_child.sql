-- Add allergies column to child table (to support Teacher UI Allergy Alerts)
ALTER TABLE child ADD COLUMN allergies VARCHAR(255) DEFAULT NULL;