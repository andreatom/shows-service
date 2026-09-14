package it.its.cinema.shows_service.model;

public class CatalogProviderUnavailableException extends RuntimeException {
    public CatalogProviderUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
