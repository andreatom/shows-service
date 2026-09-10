package it.its.cinema.shows_service.service;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.model.MovieNotFoundException;
import it.its.cinema.shows_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie findById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie with ID " + movieId + " not found"));
    }

    public List<Movie> findByTitle(String title) {
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title);
        if (movies.isEmpty()) {
            throw new MovieNotFoundException("Movie with title " + title + " not found");
        }
        return movies;
    }

    public Movie create(String title,int durationMinutes) {
        Movie createdMovie = movieRepository.save(new Movie(null,title, durationMinutes));
        log.info("Created movie {} with ID {}", title, createdMovie.getId());
        return createdMovie;
    }

    public void delete(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie with ID " + movieId + " not found");
        }
        movieRepository.deleteById(movieId);
    }



}
