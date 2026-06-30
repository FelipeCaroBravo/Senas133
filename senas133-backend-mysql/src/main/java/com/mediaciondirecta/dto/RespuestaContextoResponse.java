package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.RespuestaContextoEmergencia;

import java.time.LocalDateTime;

public record RespuestaContextoResponse(
        Long id,
        Long emergenciaId,
        String codigoPregunta,
        String textoPregunta,
        Boolean respuesta,
        Integer orden,
        boolean modoCamuflaje,
        LocalDateTime creadoEn
) {
    public static RespuestaContextoResponse from(RespuestaContextoEmergencia respuesta) {
        return new RespuestaContextoResponse(
                respuesta.getId(),
                respuesta.getEmergencia().getId(),
                respuesta.getCodigoPregunta(),
                respuesta.getTextoPregunta(),
                respuesta.getRespuesta(),
                respuesta.getOrden(),
                respuesta.isModoCamuflaje(),
                respuesta.getCreadoEn()
        );
    }
}
