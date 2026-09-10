package it.its.cinema.shows_service.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;


    @Operation(summary = "Recupera tutti i film")
    @ApiResponse(responseCode = "200", description = "Lista dei film recuperata con successo")
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.findAll();
    }

    @Operation(summary = "Recupera un film tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film trovato"),
            @ApiResponse(responseCode = "404", description = "Film non trovato", content = @Content)
    })
    @GetMapping("/{id}")
    public Movie getMovieById(
            @Parameter(description = "ID del film da cercare") @PathVariable Long id
    ) {
        return movieService.findById(id);
    }

    @Operation(summary = "Cerca film per titolo (ricerca parziale, case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista dei film trovati"),
            @ApiResponse(responseCode = "404", description = "Nessun film trovato", content = @Content)
    })
    @GetMapping("/search/{title}")
    public List<Movie> searchMoviesByTitle(
            @Parameter(description = "Testo da cercare nel titolo") @PathVariable String title
    ) {
        return movieService.findByTitle(title);
    }

    @Operation(summary = "Crea un nuovo film")
    @ApiResponse(responseCode = "201", description = "Film creato con successo")
    @PostMapping("/create")
    public ResponseEntity<Movie> createMovie(
            @RequestBody Movie request
    ) {
        Movie created = movieService.create(
                request.getTitle(),
                request.getDurationMinutes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Elimina un film tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Film eliminato con successo"),
            @ApiResponse(responseCode = "404", description = "Film non trovato", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "ID del film da eliminare") @PathVariable Long id
    ) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
