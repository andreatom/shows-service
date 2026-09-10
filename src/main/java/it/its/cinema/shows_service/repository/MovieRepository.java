package it.its.cinema.shows_service.repository;

import it.its.cinema.shows_service.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAllBy(Pageable pageable);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
