ALTER TABLE admin
    DROP COLUMN center_name,
    DROP COLUMN capacity,
    ADD COLUMN designation VARCHAR(100),
    ADD COLUMN branch_name VARCHAR(50);

ALTER TABLE director_registration_request
    DROP COLUMN center_name,
    DROP COLUMN capacity,
    ADD COLUMN designation VARCHAR(255),
    ADD COLUMN branch_name VARCHAR(255);
