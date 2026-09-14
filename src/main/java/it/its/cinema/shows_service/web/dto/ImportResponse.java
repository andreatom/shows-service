package it.its.cinema.shows_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ImportResponse(
        @Schema(description = "Film nuovi effettivamente inseriti", example = "4")
        int importati,

        @Schema(description = "Film del fornitore che erano già in catalogo", example = "3")
        int giaPresenti,

        @Schema(description = "Film offerti dal fornitore", example = "7")
        int totaleDalFornitore) {
}
