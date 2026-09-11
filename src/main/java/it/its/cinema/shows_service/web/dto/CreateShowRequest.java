package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "I dati per creare uno spettacolo")
public record CreateShowRequest(
        @NotNull(message = "Il film è obbligatorio")
        @Schema(description = "Identificativo di un film già in catalogo",
        example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long movieId,

        @NotNull(message = "L'orario di inizio è obbligatorio")
        @Future(message = "L'orario di inizio deve essere nel futuro")
        @Schema(description = "Data e ora di inizio in ISO-8601. Deve essere futura",
                example = "2023-10-10T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startTime,

        @NotNull(message = "Il prezzo base è obbligatorio")
        @DecimalMin(value = "0.0", message = "Il prezzo base non può essere negativo")
        @Schema(description = "Prezzo base del biglietto in euro",
                example = "9.50", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal basePrice,

        @NotNull(message = "I posti totali sono obbligatori")
        @Positive(message = "I posti totali devono essere positivi")
        @Schema(description = "Posti totali della sala",
                example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer totalSeats
) {
}
