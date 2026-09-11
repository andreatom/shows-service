package it.its.cinema.shows_service.service;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.model.MovieInUseException;
import it.its.cinema.shows_service.model.MovieNotFoundException;
import it.its.cinema.shows_service.model.MovieTitleAlreadyExistsException;
import it.its.cinema.shows_service.repository.MovieRepository;
import it.its.cinema.shows_service.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    @Transactional(readOnly = true)
    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAllBy(pageable);
    }

    @Transactional(readOnly = true)
    public Movie findById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie with ID " + movieId + " not found"));
    }

    @Transactional(readOnly = true)
    public Page<Movie> findByTitle(String title, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        if (movies.isEmpty()) {
            throw new MovieNotFoundException("Movie with title " + title + " not found");
        }
        return movies;
    }

    @Transactional(readOnly = true)
    public Page<Movie> filterByTitle(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Transactional
    public Movie create(String title, int durationMinutes) {
        if (movieRepository.existsByTitleIgnoreCase(title)) {
            throw new MovieTitleAlreadyExistsException(title);
        }
        Movie createdMovie = movieRepository.save(new Movie(null, title, durationMinutes));
        log.info("Created movie {} with ID {}", title, createdMovie.getId());
        return createdMovie;
    }

    @Transactional
    public void delete(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie with ID " + movieId + " not found");
        }
        if (showRepository.existsByMovieId(movieId)) {
            throw new MovieInUseException(movieId);
        }
        movieRepository.deleteById(movieId);
    }

}
