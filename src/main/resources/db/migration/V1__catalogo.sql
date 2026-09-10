CREATE TABLE movies(
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    minutes INT NOT NULL,
    CONSTRAINT  uk_movies_title UNIQUE(title)
);

CREATE TABLE shows(
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id),
    start_time TIMESTAMP NOT NULL,
    base_price NUMERIC(8, 2) NOT NULL CHECK ( base_price >= 0 ),
    total_seats INT NOT NULL CHECK ( total_seats > 0),
    available_seats INT NOT NULL CHECK ( available_seats >= 0 )
);