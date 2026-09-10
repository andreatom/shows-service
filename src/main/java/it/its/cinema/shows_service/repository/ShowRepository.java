package it.its.cinema.shows_service.repository;

import it.its.cinema.shows_service.model.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface ShowRepository extends JpaRepository<Show, Long> {
    @EntityGraph(attributePaths = "movie")
    Page<Show> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "movie")
    Optional<Show> findWithMovieById(Long id);

    // Niente JOIN FETCH qui: con un Pageable costringerebbe Hibernate a paginare
    // in memoria (HHH000104). Il film viene caricato con l'entity graph.
    @EntityGraph(attributePaths = "movie")
    @Query("""
    SELECT s FROM Show s
    WHERE s.movie.id = :movieId
    AND s.startTime BETWEEN :da AND :a
    """)
    Page<Show> findByFilmAndInterval(@Param("movieId")Long movieId,
                                     @Param("da") LocalDateTime da,
                                     @Param("a")LocalDateTime a,
                                     Pageable pageable
                                     );
}
