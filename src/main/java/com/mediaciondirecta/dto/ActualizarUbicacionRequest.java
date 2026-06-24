package com.mediaciondirecta.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ActualizarUbicacionRequest(
        @NotNull @Min(-90) @Max(90) Double latitud,
        @NotNull @Min(-180) @Max(180) Double longitud,
        Double precisionMetros,
        String fuente
) {
}
