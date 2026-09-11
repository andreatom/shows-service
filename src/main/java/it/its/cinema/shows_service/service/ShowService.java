package it.its.cinema.shows_service.service;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.model.MovieNotFoundException;
import it.its.cinema.shows_service.model.Show;
import it.its.cinema.shows_service.model.ShowNotFoundException;
import it.its.cinema.shows_service.repository.MovieRepository;
import it.its.cinema.shows_service.repository.ShowRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowRepository repository;
    private final MovieRepository movieRepository;

    @Transactional
    public Show addShow(Long moviedId, LocalDateTime startTime, int totalSeats, BigDecimal basePrice) {
        if (moviedId == null) {
            throw new IllegalArgumentException("Movie ID is required");
        }
        Movie movie = movieRepository.findById(moviedId)
                .orElseThrow(()-> new MovieNotFoundException("Movie with ID " + moviedId + " not found"));

        Show creato = repository.save(new Show(null, movie, startTime, basePrice, totalSeats));
        log.info("Creato spettacolo {} per il film {} con {} posti disponibili",
                creato.getId(), movie.getTitle(), totalSeats);
        return creato;
    }

    @Transactional(readOnly = true)
    public Page<Show> findAll(Pageable pageable) {
        return repository.findAllBy(pageable);
    }

    @Transactional(readOnly = true)
    public Show perId(Long id) {
        return repository.findWithMovieById(id)
                .orElseThrow(() -> new ShowNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Show> perFilmEIntervallo(Long movieId, LocalDateTime da, LocalDateTime a, Pageable pageable) {
        return repository.findByFilmAndInterval(movieId, da, a, pageable);
    }

    @Transactional(readOnly = true)
    public Show findById(Long id) {
        return repository.findWithMovieById(id)
                .orElseThrow(() -> new ShowNotFoundException(id));
    }

    @Transactional
    public Show reserveSeats(Long showId, int quantity) {
        Show show = findById(showId);
        show.reserveSeats(quantity);
        log.info("riservati {} posti sullo spettacolo {}, ne restano {}",
                quantity, showId, show.getAvailableSeats());
        return repository.save(show);
    }

    @Transactional
    public Show releaseSeats(Long showId, int quantity) {
        Show show = findById(showId);
        show.releaseSeats(quantity);
        log.info("rilasciati {} posti sullo spettacolo {}, ne restano {}",
                quantity, showId, show.getAvailableSeats());
        return repository.save(show);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ShowNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Show updateShow(Long id, LocalDateTime startTime, BigDecimal basePrice) {
        Show show = findById(id);
        show.setStartTime(startTime);
        show.setBasePrice(basePrice);
        return repository.save(show);
    }
}
