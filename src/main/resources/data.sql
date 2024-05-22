INSERT INTO Users (username, password, role)
SELECT 'admin', '$2a$12$gxckVkQ2dURpmnE9D0XgzOUrLy1YXjojGh8TnUUe9OeGCvyye.8f6', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM Users);