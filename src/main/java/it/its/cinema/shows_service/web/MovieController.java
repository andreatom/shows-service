package it.its.cinema.shows_service.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.its.cinema.shows_service.model.Movie;
import it.its.cinema.shows_service.service.MovieService;
import it.its.cinema.shows_service.web.dto.MovieRequest;
import it.its.cinema.shows_service.web.dto.MovieResponse;
import it.its.cinema.shows_service.web.mapper.MovieMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;


    @Operation(summary = "Recupera tutti i film")
    @ApiResponse(responseCode = "200", description = "Lista dei film recuperata con successo")
    @GetMapping
    public Page<MovieResponse> getAllMovies(
            @Parameter(description = "Frammento di titolo da cercare", example = "dune")
            @RequestParam(required = false) String title,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return movieMapper.toResponse(
                title == null
                        ? movieService.findAll(pageable)
                        : movieService.filterByTitle(title, pageable));
    }

    @Operation(summary = "Recupera un film tramite ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film trovato"),
            @ApiResponse(responseCode = "404", description = "Film non trovato", content = @Content)
    })
    @GetMapping("/{id}")
    public MovieResponse getMovieById(
            @Parameter(description = "ID del film da cercare")
            @PathVariable Long id
    ) {
        return movieMapper.toResponse(movieService.findById(id));
    }

    @Operation(summary = "Cerca film per titolo (ricerca parziale, case-insensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista dei film trovati"),
            @ApiResponse(responseCode = "404", description = "Nessun film trovato", content = @Content)
    })
    @GetMapping("/search/{title}")
    public Page<MovieResponse> searchMoviesByTitle(
            @Parameter(description = "Testo da cercare nel titolo") @PathVariable String title,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return movieMapper.toResponse(movieService.findByTitle(title, pageable));
    }

    @Operation(summary = "Crea un nuovo film")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dati del nuovo film",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MovieRequest.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Film creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati del film non validi", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<MovieResponse> createMovie(
            @Valid @RequestBody MovieRequest request
    ) {
        Movie created = movieService.create(
                request.title(),
                request.durationMinutes()
        );
        return ResponseEntity.created(URI.create("/api/movies/" + created.getId()))
                .body(movieMapper.toResponse(created));
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
