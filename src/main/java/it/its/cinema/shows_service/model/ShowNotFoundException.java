package it.its.cinema.shows_service.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShowNotFoundException extends RuntimeException {

    public ShowNotFoundException(Long id) {
        super("Show not found: " + id);
    }
}
