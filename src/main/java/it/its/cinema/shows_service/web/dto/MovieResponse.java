package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Un film in catalogo")
public record MovieResponse(
        @Schema(description = "Identificativo del film", example = "1")
        Long id,

        @Schema(description = "Titolo", example = "Il Signore degli Anelli: La Compagnia dell'Anello")
        String title,

        @Schema(description = "Durata in minuti", example = "178")
        int durationMinutes,

        @Schema(description = "Contatore per il lock ottimistico", example = "0")
        Long version) {
}
