package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.SolicitudInterprete;
import com.mediaciondirecta.enums.EstadoSolicitudInterprete;

import java.time.LocalDateTime;

public record SolicitudInterpreteResponse(
        Long id,
        Long emergenciaId,
        EstadoSolicitudInterprete estado,
        String nombreInterprete,
        String urlSalaVideo,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
    public static SolicitudInterpreteResponse from(SolicitudInterprete solicitud) {
        return new SolicitudInterpreteResponse(
                solicitud.getId(),
                solicitud.getEmergencia().getId(),
                solicitud.getEstado(),
                solicitud.getNombreInterprete(),
                solicitud.getUrlSalaVideo(),
                solicitud.getCreadoEn(),
                solicitud.getActualizadoEn()
        );
    }
}
