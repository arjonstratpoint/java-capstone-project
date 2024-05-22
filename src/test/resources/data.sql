INSERT INTO Users (username, password, role)
SELECT 'admin', '$2a$12$gxckVkQ2dURpmnE9D0XgzOUrLy1YXjojGh8TnUUe9OeGCvyye.8f6', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM Users WHERE username = 'admin');

INSERT INTO Users (username, password, role)
SELECT 'user', '$2a$12$5Q0O2w8NjeyHQ0dkQDw5ceEaRPhf5.49cjEyfLxDEazk//TXOpdpW', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM Users WHERE username = 'user');


INSERT INTO Content (user_id, title, description, status, content_type, url)
VALUES
((SELECT id FROM Users WHERE username = 'user'), 'First Content Title', 'This is the description for the first content.', 'COMPLETED', 'ARTICLE', 'http://example.com/first-content'),
((SELECT id FROM Users WHERE username = 'user'), 'Second Content Title', 'This is the description for the second content.', 'COMPLETED', 'ARTICLE', 'http://example.com/second-content'),
((SELECT id FROM Users WHERE username = 'user'), 'Third Content Title', 'This is the description for the third content.', 'COMPLETED', 'ARTICLE', 'http://example.com/third-content');


INSERT INTO Content (user_id, title, description, status, content_type, url)
VALUES
((SELECT id FROM Users WHERE username = 'admin'), 'First Content Title', 'This is the description for the first content.', 'COMPLETED', 'ARTICLE', 'http://example.com/first-content'),
((SELECT id FROM Users WHERE username = 'admin'), 'Second Content Title', 'This is the description for the second content.', 'COMPLETED', 'ARTICLE', 'http://example.com/second-content'),
((SELECT id FROM Users WHERE username = 'admin'), 'Third Content Title', 'This is the description for the third content.', 'COMPLETED', 'ARTICLE', 'http://example.com/third-content');