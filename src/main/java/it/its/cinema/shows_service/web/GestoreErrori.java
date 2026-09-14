package it.its.cinema.shows_service.web;

import it.its.cinema.shows_service.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GestoreErrori extends ResponseEntityExceptionHandler {

    private static final String BASE_TYPE = "https://cinema.its.it/errori/";

    private ProblemDetail problema(HttpStatus stato, String titolo, String tipo, String dettaglio) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(stato, dettaglio);
        p.setTitle(titolo);
        p.setType(URI.create(BASE_TYPE + tipo));
        return p;
    }

     @ExceptionHandler(CatalogProviderUnavailableException.class)
     public ResponseEntity<ProblemDetail> fornitoreNonDisponibile(
             CatalogProviderUnavailableException e) {

        ProblemDetail corpo = problema(HttpStatus.SERVICE_UNAVAILABLE,
                "Fornitore non disponibile",
                "fornitore-non-disponibile",
                e.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, "30")
                .body(corpo);
     }

    @ExceptionHandler({ShowNotFoundException.class, MovieNotFoundException.class})
    public ProblemDetail nonTrovato(RuntimeException e) {
        boolean film = e instanceof MovieNotFoundException;
        return problema(HttpStatus.NOT_FOUND,
                film ? "Film non trovato" : "Spettacolo non trovato",
                film ? "film-non-trovato" : "spettacolo-non-trovato",
                e.getMessage());
    }

    @ExceptionHandler(NotEnoughSeatsException.class)
    public ProblemDetail postiInsufficienti(NotEnoughSeatsException e) {
        return problema(HttpStatus.CONFLICT, "Posti insufficienti",
                "posti-insufficienti", e.getMessage());
    }

    @ExceptionHandler(MovieTitleAlreadyExistsException.class)
    public ProblemDetail titoloDuplicato(MovieTitleAlreadyExistsException e) {
        return problema(HttpStatus.CONFLICT, "Titolo gia' in catalogo",
                "titolo-duplicato", e.getMessage());
    }

    @ExceptionHandler(MovieInUseException.class)
    public ProblemDetail filmInProgrammazione(MovieInUseException e) {
        return problema(HttpStatus.CONFLICT, "Film in programmazione",
                "film-in-programmazione", e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail conflittoDiConcorrenza(OptimisticLockingFailureException e) {
        return problema(HttpStatus.CONFLICT, "Modifica concorrente",
                "modifica-concorrente",
                "Qualcun altro ha modificato la risorsa mentre completavi "
                        + "l'operazione. Rileggila e riprova.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail richiestaNonValida(IllegalArgumentException e) {
        return problema(HttpStatus.BAD_REQUEST, "Richiesta non valida",
                "richiesta-non-valida", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tipoSbagliato(MethodArgumentTypeMismatchException e) {
        return problema(HttpStatus.BAD_REQUEST, "Parametro non valido",
                "parametro-non-valido",
                "Il parametro '" + e.getName() + "' non accetta il valore '"
                        + e.getValue() + "'.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // LinkedHashMap: l'ordine dei campi resta quello di dichiarazione,
        // e un JSON di errore che cambia ordine a ogni chiamata e' scomodo
        // da leggere e impossibile da confrontare in un test.
        Map<String, String> campi = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(errore ->
                campi.putIfAbsent(errore.getField(), errore.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors().forEach(errore ->
                campi.putIfAbsent(errore.getObjectName(), errore.getDefaultMessage()));

        ProblemDetail corpo = problema(HttpStatus.BAD_REQUEST, "Dati non validi",
                "dati-non-validi",
                "La richiesta contiene " + campi.size() + " campo/i non valido/i.");
        corpo.setProperty("errors", campi);

        return handleExceptionInternal(ex, corpo, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail erroreInterno(Exception e) {
        log.error("Errore non gestito", e);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Errore interno",
                "errore-interno",
                "Si e' verificato un errore imprevisto. Se il problema persiste, "
                        + "segnalalo indicando l'ora esatta del tentativo.");
    }
}
