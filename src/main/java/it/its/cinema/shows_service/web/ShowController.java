package it.its.cinema.shows_service.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.its.cinema.shows_service.model.Show;
import it.its.cinema.shows_service.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @Operation(
            summary = "Elenca gli spettacoli",
            description = "Restituisce tutti gli spettacoli in programmazione, "
                    + "ordinati per orario di inizio crescente. "
                    + "Dal G3 questa rotta sarà paginata."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Elenco restituito, anche vuoto",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Show.class))
                    )
            )
    })

    @GetMapping
    public Page<Show> findAll(
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return showService.findAll(pageable);
    }

    @Operation(
            summary = "Cerca uno spettacolo per ID",
            description = "Restituisce il singolo spettacolo con il film associato, "
                    + "il prezzo base e i posti ancora disponibili."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spettacolo trovato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Show.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Spettacolo non trovato",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "L'ID non è un numero",
                    content = @Content
            )
    })

    @GetMapping("/findById")
    public Show findById(
            @RequestParam Long id
    ) {
        return showService.findById(id);
    }

    @GetMapping("/ricerca")
    public Page<Show> ricerca(
            @Parameter(description = "Identificativo del film", example = "1")
            @RequestParam Long movieId,
            @Parameter(description = "Inizio dell'intervallo", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime da,
            @Parameter(description = "Fine dell'intervallo", example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime a,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC)
            Pageable pageable
    ){
        return showService.perFilmEIntervallo(movieId, da, a, pageable);
    }




    @Operation(
            summary = "Elimina uno spettacolo per ID",
            description = "Elimina lo spettacolo identificato dall'ID fornito."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Richiesta di eliminazione elaborata",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Spettacolo non trovato",
                    content = @Content
            )
    })

    @DeleteMapping("/deleteById")
    public ResponseEntity<Void> deleteById(
            @RequestParam Long id
    ) {
        showService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Aggiunge uno spettacolo",
            description = "Salva un nuovo spettacolo e restituisce i dati inseriti, "
                    + "incluso l'ID assegnato."
    )

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dati del nuovo spettacolo",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Show.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Spettacolo aggiunto",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Show.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dati dello spettacolo non validi",
                    content = @Content
            )
    })

    @PostMapping("/add")
    public ResponseEntity<Show> create(
            @RequestBody Show richiesta
    ) {
        Show created = showService.addShow(
                richiesta.getMovie().getId(),
                richiesta.getStartTime(),
                richiesta.getTotalSeats(),
                richiesta.getBasePrice()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Aggiorna uno spettacolo",
            description = "Aggiorna i dati dello spettacolo identificato dall'ID fornito."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dati aggiornati dello spettacolo",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Show.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spettacolo aggiornato",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Show.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Spettacolo non trovato",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Dati dello spettacolo non validi",
                    content = @Content
            )
    })
    @PutMapping("/update")
    public Show update(
            @RequestBody Show show
    ) {
        return showService.updateShow(show);
    }

    @Operation(
            summary = "Prenota posti per uno spettacolo",
            description = "Riduce il numero di posti disponibili dello spettacolo della quantità richiesta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Posti prenotati",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Show.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Spettacolo non trovato",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "ID o quantità non validi",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Posti insufficienti o quantità non positiva",
                    content = @Content
            )
    })
    @PostMapping("/{id}/reserve")
    public Show reserveSeats(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID dello spettacolo", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.Parameter(description = "Numero di posti da prenotare", example = "2", required = true)
            @RequestParam int quantity
    ) {
        return showService.reserveSeats(id, quantity);
    }

    @Operation(
            summary = "Rilascia posti per uno spettacolo",
            description = "Aumenta il numero di posti disponibili dello spettacolo della quantità richiesta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Posti rilasciati",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Show.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Spettacolo non trovato",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "ID o quantità non validi",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Quantità non positiva",
                    content = @Content
            )
    })
    @PostMapping("/{id}/release")
    public Show releaseSeats(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID dello spettacolo", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.Parameter(description = "Numero di posti da rilasciare", example = "2", required = true)
            @RequestParam int quantity
    ) {
        return showService.releaseSeats(id, quantity);
    }
}
