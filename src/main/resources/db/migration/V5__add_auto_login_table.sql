CREATE TABLE auto_login
(
    id    VARCHAR(40) PRIMARY KEY,
    email VARCHAR(255)          NOT NULL,
    role  ENUM ('ADMIN','USER') NOT NULL
);
