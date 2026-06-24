package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RespuestaCamuflajeItemRequest(
        @NotBlank String codigoPregunta,
        @NotBlank String textoPregunta,
        @NotNull Boolean respuesta,
        @NotNull Integer orden
) {
}
