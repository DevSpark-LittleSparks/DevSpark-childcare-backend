-- Add experience to teacher_registration_request
ALTER TABLE teacher_registration_request ADD COLUMN experience VARCHAR(100);

-- Create director_registration_request table
CREATE TABLE director_registration_request (
    request_id CHAR(36) PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    center_name VARCHAR(150),
    capacity INT,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_by VARCHAR(150),
    deleted BOOLEAN DEFAULT FALSE
);
