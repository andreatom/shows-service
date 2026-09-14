package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Quanti posti muovere, e per conto di quale saga")
public record SeatsRequest(

        @NotBlank(message = "Il sagaId e' obbligatorio")
        @Schema(description = "Identificativo dell'operazione di acquisto, "
                + "generato da chi la coordina e uguale per tutti i suoi passi",
                example = "3f2a1b9c-6d4e-4a7b-9c2f-1e8d0a5b7c31",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String sagaId,

        @NotNull(message = "La quantita' e' obbligatoria")
        @Positive(message = "La quantita' deve essere positiva")
        @Schema(description = "Quanti posti", example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer quantity) {
}
