CREATE TABLE users (
                       id             BIGSERIAL PRIMARY KEY,
                       user_name      VARCHAR(50)  NOT NULL UNIQUE,
                       email          VARCHAR(255) NOT NULL UNIQUE,
                       password_hash  VARCHAR(255) NOT NULL,
                       role           VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       enabled        BOOLEAN      NOT NULL DEFAULT true,
                       created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);