package it.its.cinema.shows_service.web.mapper;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.web.dto.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDurationMinutes(),
                movie.getVersion()
        );
    }

    public Page<MovieResponse> toResponse(Page<Movie> movies) {
        return  movies.map(this::toResponse);
    }

}
