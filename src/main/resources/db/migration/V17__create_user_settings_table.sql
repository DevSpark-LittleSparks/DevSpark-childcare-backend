CREATE TABLE user_settings (
    email VARCHAR(150) NOT NULL PRIMARY KEY,
    theme VARCHAR(20) NOT NULL,
    language VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    timezone VARCHAR(50) NOT NULL
);
 