package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.EstadoEmergencia;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull EstadoEmergencia estado
) {
}
