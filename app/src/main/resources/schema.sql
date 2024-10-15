DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP
);

CREATE TABLE url_checks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    url_id INT REFERENCES urls(id) ON DELETE CASCADE,
    statusCode INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description TEXT,
    createdAt TIMESTAMP
);
