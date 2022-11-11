CREATE TABLE users
(
    id             BIGSERIAL    NOT NULL CONSTRAINT user_pk PRIMARY KEY,
    first_name     VARCHAR(32)  NOT NULL,
    last_name      VARCHAR(32)  NOT NULL,
    date_of_birth  DATE         NOT NULL,
    email          VARCHAR(128) NOT NULL,
    password       VARCHAR(128) NOT NULL,
    enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    active         BOOLEAN      NOT NULL DEFAULT FALSE,
    create_at      TIMESTAMP    NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
);