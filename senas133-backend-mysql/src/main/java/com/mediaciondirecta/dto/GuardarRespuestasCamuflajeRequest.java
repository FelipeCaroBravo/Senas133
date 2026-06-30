package com.mediaciondirecta.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GuardarRespuestasCamuflajeRequest(
        @NotEmpty List<@Valid RespuestaCamuflajeItemRequest> respuestas
) {
}
