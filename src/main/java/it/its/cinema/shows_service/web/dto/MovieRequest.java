package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "I dati di un film")
public record MovieRequest(
        @NotBlank(message = "Il titolo è obbligatorio")
        @Size(max = 200, message = "Il titolo non può superare i 200 caratteri")
        @Schema(description = "Il titolo del film, unico in catalogo",
        example = "Il Signore degli Anelli: La Compagnia dell'Anello", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @NotNull(message = "La durata è obbligatoria")
        @Positive(message = "La durata deve essere un numero positivo")
        @Schema(description = "La durata del film in minuti",
        example = "178", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer durationMinutes) {
}
