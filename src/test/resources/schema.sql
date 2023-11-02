DROP TABLE ktube_user IF EXISTS;
DROP INDEX ktube_user_idx_email IF EXISTS;

CREATE TABLE ktube_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(15) NOT NULL,
    role SMALLINT NOT NULL
);

CREATE UNIQUE INDEX ktube_user_idx_email ON ktube_user (email);