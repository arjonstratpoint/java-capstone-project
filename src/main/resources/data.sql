INSERT INTO Users (username, password, role)
SELECT 'admin', '$2a$12$/AmJsfhBPKsdRGfH5NhuDOdBt7CdC8Vrhyk2oRYxPXyOzHKlEyc/e', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM Users);