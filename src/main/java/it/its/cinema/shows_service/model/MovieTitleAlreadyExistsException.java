package it.its.cinema.shows_service.model;

public class MovieTitleAlreadyExistsException extends RuntimeException {
    public MovieTitleAlreadyExistsException(String message) {
        super("Esiste già un film con questo titolo: " + message);
    }
}
