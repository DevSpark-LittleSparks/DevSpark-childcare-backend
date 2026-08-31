-- Adding the 'category' column to the 'activity' table created in V1
ALTER TABLE activity ADD COLUMN category VARCHAR(100) NOT NULL DEFAULT 'General' AFTER activity_name;