ALTER TABLE shows ADD COLUMN  version BIGINT NOT NULL DEFAULT 0;

CREATE INDEX idx_shows_movie_start ON shows(movie_id, start_time);