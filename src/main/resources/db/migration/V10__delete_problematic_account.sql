-- Migration to delete problematic account and linked records
SET @email = 'pasanhasanka21@gmail.com';

-- Delete from parent_registration_request
DELETE FROM parent_registration_request WHERE email = @email;

-- Delete from parent (if exists)
DELETE FROM parent WHERE account_id IN (SELECT account_id FROM account WHERE email = @email);

-- Delete from otp_token
DELETE FROM otp_token WHERE account_id IN (SELECT account_id FROM account WHERE email = @email);

-- Delete from account
DELETE FROM account WHERE email = @email;
