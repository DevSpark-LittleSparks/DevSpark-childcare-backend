-- ================================================================
--  File    : V40__add_parent_stripe_customer_id.sql
--  Purpose : Saved cards were stored as bare PaymentMethod ids that were
--            never attached to a Stripe Customer. Stripe allows an
--            unattached PaymentMethod to back exactly one PaymentIntent,
--            so the first charge succeeded and every later charge failed
--            with "The provided PaymentMethod was previously used ...
--            you must attach it to a Customer first".
--
--            One Stripe Customer per parent, reused for every card and
--            charge, is what makes a saved card reusable.
--
--  Note    : Written to be idempotent. At least one developer database
--            already had this column added by hand outside Flyway, so a
--            plain ADD COLUMN fails there with "Duplicate column name".
--            The INFORMATION_SCHEMA guard is used in preference to
--            "ADD COLUMN IF NOT EXISTS" because that spelling is a
--            MariaDB extension MySQL 8 rejects, and this project is
--            currently developed against both engines.
-- ================================================================

SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'parent'
      AND COLUMN_NAME  = 'stripe_customer_id'
);

SET @ddl = IF(@column_exists = 0,
    'ALTER TABLE parent ADD COLUMN stripe_customer_id VARCHAR(255) NULL',
    'DO 0');

PREPARE add_stripe_customer_id FROM @ddl;
EXECUTE add_stripe_customer_id;
DEALLOCATE PREPARE add_stripe_customer_id;
