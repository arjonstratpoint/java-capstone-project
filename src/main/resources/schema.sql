CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Content (
    id SERIAL PRIMARY KEY ,
    user_id INT,
    title varchar(255) NOT NULL,
    description text,
    status VARCHAR(20) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_updated TIMESTAMP,
    url VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);