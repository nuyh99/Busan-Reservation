CREATE TABLE IF NOT EXISTS member
(
    company  VARCHAR(255)               NOT NULL,
    email    VARCHAR(255) PRIMARY KEY,
    name     VARCHAR(255)               NOT NULL,
    password VARCHAR(255)               NOT NULL,
    phone    VARCHAR(255)               NOT NULL,
    region   ENUM ('BUSAN','GANGNEUNG') NOT NULL,
    role     ENUM ('ADMIN','USER')      NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS reservation
(
    created_time      DATETIME(6)                  NOT NULL,
    end_time          DATETIME(6)                  NOT NULL,
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id           BIGINT                       NOT NULL,
    start_time        DATETIME(6)                  NOT NULL,
    cancel_reason     VARCHAR(1000),
    reservation_email VARCHAR(255)                 NOT NULL,
    status            ENUM ('CANCELED','RESERVED') NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS room
(
    max_people_count INT           NOT NULL,
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    image            VARCHAR(1000) NOT NULL,
    name             VARCHAR(255)  NOT NULL
) ENGINE = InnoDB;
