package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Uno spettacolo in programmazione")
public record ShowResponse(
        @Schema(description = "identificativo dello spettacolo", example = "1")
        Long id,

        @Schema(description = "Identificativo del film proiettato", example = "1")
        Long movieId,

        @Schema(description = "Titolo del film proiettato", example = "Il Signore degli Anelli")
        String movieTitle,

        @Schema(description = "Data e ora di inizio", example = "2023-10-10T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "Prezzo base del biglietto, in euro", example = "12.00")
        BigDecimal basePrice,

        @Schema(description = "Posti totali della sala", example = "100")
        int totalSeats,

        @Schema(description = "Posti ancora disponibili", example = "50")
        int availableSeats,

        @Schema(description = "Vero dalle 20:00 in poi", example = "true")
        boolean eveningShow,

        @Schema(description = "Contatore per il lock ottimistico", example = "0")
        Long version) {
}
