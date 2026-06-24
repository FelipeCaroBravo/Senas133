package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.UbicacionEmergencia;

import java.time.LocalDateTime;

public record UbicacionResponse(
        Long id,
        Long emergenciaId,
        Double latitud,
        Double longitud,
        Double precisionMetros,
        String fuente,
        LocalDateTime creadoEn
) {
    public static UbicacionResponse from(UbicacionEmergencia ubicacion) {
        return new UbicacionResponse(
                ubicacion.getId(),
                ubicacion.getEmergencia().getId(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                ubicacion.getPrecisionMetros(),
                ubicacion.getFuente(),
                ubicacion.getCreadoEn()
        );
    }
}
