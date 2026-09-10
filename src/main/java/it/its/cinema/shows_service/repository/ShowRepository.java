package it.its.cinema.shows_service.repository;

import it.its.cinema.shows_service.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShowRepository extends JpaRepository<Show, Long> {

}
