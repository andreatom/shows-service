package it.its.cinema.shows_service.service;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.model.MovieNotFoundException;
import it.its.cinema.shows_service.model.Show;
import it.its.cinema.shows_service.model.ShowNotFoundException;
import it.its.cinema.shows_service.repository.MovieRepository;
import it.its.cinema.shows_service.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowService {

    private final ShowRepository repository;
    private final MovieRepository movieRepository;

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

    public List<Show> findAll() {
        return repository.findAll();
    }

    public Show findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ShowNotFoundException(id));
    }

    public Show reserveSeats(Long showId, int quantity) {
        Show show = findById(showId);
        show.reserveSeats(quantity);
        log.info("riservati {} posti sullo spettacolo {}, ne restano {}",
                quantity, showId, show.getAvailableSeats());
        return repository.save(show);
    }

    public Show releaseSeats(Long showId, int quantity) {
        Show show = findById(showId);
        show.releaseSeats(quantity);
        log.info("rilasciati {} posti sullo spettacolo {}, ne restano {}",
                quantity, showId, show.getAvailableSeats());
        return repository.save(show);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ShowNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public Show updateShow(Show show) {
        if (!repository.existsById(show.getId())) {
            throw new ShowNotFoundException(show.getId());
        }
        return repository.save(show);
    }
}
