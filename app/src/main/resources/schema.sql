DROP TABLE IF EXISTS urls CASCADE;
DROP TABLE IF EXISTS url_checks;

CREATE TABLE urls (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT pk_url PRIMARY KEY (id)
);

CREATE TABLE url_checks (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    url_id BIGINT REFERENCES urls (id),
    statusCode INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP,
    CONSTRAINT pk_url_checks PRIMARY KEY (id)
);
