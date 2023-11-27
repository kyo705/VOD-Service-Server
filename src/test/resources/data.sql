INSERT INTO ktube_user (id, email, password, nickname, grade, security_level)
VALUES (1, 'email@naver.com', '$2a$10$63n/krKPm9BiaRlDkJjLXOCdzt28m/K2kmMUEu7JYcNLu6zU3Gap2', 'hi', 0, 0),
        (2, 'email1@naver.com', '$2a$10$63n/krKPm9BiaRlDkJjLXOCdzt28m/K2kmMUEu7JYcNLu6zU3Gap2', 'hi', 1, 0),
        (3, 'email2@naver.com', '$2a$10$63n/krKPm9BiaRlDkJjLXOCdzt28m/K2kmMUEu7JYcNLu6zU3Gap2', 'hi', 2, 0),
        (4, 'email3@naver.com', '$2a$10$63n/krKPm9BiaRlDkJjLXOCdzt28m/K2kmMUEu7JYcNLu6zU3Gap2', 'hi', 1, 2);

INSERT INTO user_device (id, user_id, device_info)
VALUES (1, 2, 'Mozilla/1'),
        (2, 2, 'AppleWebKit/2'),
        (3, 2, 'Chrome/3'),
        (4, 2, 'Safari/4'),
        (5, 3, 'Mozilla/1'),
        (6, 3, 'AppleWebKit/2'),
        (7, 3, 'Chrome/3'),
        (8, 3, 'Safari/4');

INSERT INTO user_log (id, user_id, connect_ip, connect_device, connect_type, connect_timestamp)
VALUES (1, 1, '127.0.0.1', 'device1', 0, 10000000),
        (2, 1, '59.2.1.5', 'device2', 0, 10000003),
        (3, 1, '131.52.1.33', 'device3', 0, 10000050),
        (4, 1, '59.2.1.5', 'device2', 1, 10000060),
        (5, 1, '127.0.0.1', 'device1', 1, 10007000),
        (6, 1, '131.52.1.33', 'device3', 1, 11000000);