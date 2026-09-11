package it.its.cinema.shows_service.model;

public class MovieInUseException extends RuntimeException {
    public MovieInUseException(Long id) {
        super("Il film " + id + " e' in programmazione: elimina prima i suoi spettacoli");
    }
}
