package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "I dati per aggiornare uno spettacolo")
public record UpdateShowRequest(

        @NotNull(message = "L'orario di inizio è obbligatorio")
        @Future(message = "L'orario di inizio deve essere nel futuro")
        @Schema(description = "L'orario di inizio dello spettacolo in ISO-8601. Deve essere futura",
                example = "2023-10-10T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startTime,


        @NotNull(message = "Il prezzo base è obbligatorio")
        @DecimalMin(value = "0.0",message = "Il prezzo base non può essere negativo")
        @Schema(description = "Nuovo prezzo base, in euro",
                example = "12.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal basePrice) {
}
