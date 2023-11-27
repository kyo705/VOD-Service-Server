DROP TABLE ktube_user IF EXISTS;
DROP TABLE user_device IF EXISTS;
DROP INDEX ktube_user_idx_email IF EXISTS;
DROP TABLE user_log IF EXISTS;
DROP INDEX user_log_idx_user_id IF EXISTS;

CREATE TABLE ktube_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(15) NOT NULL,
    grade SMALLINT NOT NULL,
    security_level SMALLINT NOT NULL
);

CREATE TABLE user_device (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_info VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX ktube_user_idx_email ON ktube_user (email);

CREATE TABLE user_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    connect_ip VARCHAR(50) NOT NULL,
    connect_device VARCHAR(50) NOT NULL,
    connect_type SMALLINT NOT NULL,
    connect_timestamp BIGINT NOT NULL
);

CREATE INDEX user_log_idx_user_id ON user_log (user_id);
