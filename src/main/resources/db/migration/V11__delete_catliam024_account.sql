-- Migration to delete catliam024@gmail.com and its associated child records
SET @email = 'catliam024@gmail.com';

-- Delete child records linked to this email
DELETE FROM child WHERE guardian_email = @email;

-- Delete parent registration requests
DELETE FROM parent_registration_request WHERE email = @email;

-- Delete from parent (if exists)
DELETE FROM parent WHERE account_id IN (SELECT account_id FROM account WHERE email = @email);

-- Delete from otp_token
DELETE FROM otp_token WHERE account_id IN (SELECT account_id FROM account WHERE email = @email);

-- Delete from account
DELETE FROM account WHERE email = @email;
