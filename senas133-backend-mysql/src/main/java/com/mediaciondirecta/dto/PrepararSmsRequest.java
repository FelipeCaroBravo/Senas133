package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;
import jakarta.validation.constraints.NotNull;

public record PrepararSmsRequest(
        Long usuarioId,
        @NotNull TipoEmergencia tipo,
        SubtipoIncidente subtipo,
        Double latitud,
        Double longitud,
        Double precisionMetros,
        String mensajeCorto,
        boolean modoCamuflaje
) {
}
