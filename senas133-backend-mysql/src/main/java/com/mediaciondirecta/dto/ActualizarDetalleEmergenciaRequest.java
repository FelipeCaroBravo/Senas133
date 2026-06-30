package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.SubtipoIncidente;
import jakarta.validation.constraints.NotNull;

public record ActualizarDetalleEmergenciaRequest(
        @NotNull SubtipoIncidente subtipo,
        String mensaje,
        String fraseCodigo,
        Boolean lugarSeguroParaMasInformacion
) {
}
