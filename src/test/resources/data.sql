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