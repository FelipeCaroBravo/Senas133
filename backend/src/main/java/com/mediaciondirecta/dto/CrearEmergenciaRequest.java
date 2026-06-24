package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;
import jakarta.validation.constraints.NotNull;

public record CrearEmergenciaRequest(
        @NotNull TipoEmergencia tipo,
        SubtipoIncidente subtipo,
        String mensaje,
        String fraseCodigo,
        boolean modoCamuflaje,
        boolean requiereInterprete,
        CanalAlerta canal,
        Double latitud,
        Double longitud,
        Double precisionMetros
) {
}
