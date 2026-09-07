package it.its.cinema.shows_service.repository;

import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.model.Show;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class InMemoryShowRepository implements ShowRepository {

    private final Map<Long, Show> shows = new ConcurrentHashMap<>();
    private final AtomicLong sequenza = new AtomicLong(0);

    @Override
    public Show save(Show show) {
        if (show.getId() == null) {
            show.setId(sequenza.incrementAndGet());
        }
        shows.put(show.getId(), show);
        return  show;
    }

    @Override
    public Optional<Show> findById(Long id) {
        return Optional.ofNullable(shows.get(id));
    }

    @Override
    public List<Show> findAll() {
        return shows.values().stream()
                .sorted(Comparator.comparing(Show::getStartTime))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        shows.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return shows.containsKey(id);
    }

    @PostConstruct
    void datiDiEsempio(){
        Movie dune = new Movie(1L, "Dune - Parte Due", 166);
        Movie oppenheimer = new Movie(2L, "Oppenheimer", 180);

        LocalDateTime oggi = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        save(new Show(null, dune, oggi.withHour(17), new BigDecimal("8.50"), 120));
        save(new Show(null, dune, oggi.withHour(21), new BigDecimal("10.00"), 120));
        save(new Show(null, oppenheimer, oggi.withHour(20).plusDays(1), new BigDecimal("9.00"), 150));

        log.info("caricati {} spettacoli di esempio in memoria", shows.size());
    }
}
