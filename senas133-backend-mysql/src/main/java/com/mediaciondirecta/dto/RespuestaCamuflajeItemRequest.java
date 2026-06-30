package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RespuestaCamuflajeItemRequest(
        @NotBlank String codigoPregunta,
        String textoPregunta,
        @NotNull Boolean respuesta,
        Integer orden
) {
}
