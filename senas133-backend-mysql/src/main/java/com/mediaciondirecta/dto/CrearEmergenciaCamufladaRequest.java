package com.mediaciondirecta.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearEmergenciaCamufladaRequest(
        @NotNull Long usuarioId,
        @NotNull Double latitud,
        @NotNull Double longitud,
        Double precisionMetros,
        @NotEmpty List<@Valid RespuestaCamuflajeItemRequest> respuestas
) {
}
