package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.EstadoEmergencia;

public record CamuflajeEmergenciaResponse(
        Long emergenciaId,
        EstadoEmergencia estado,
        String mensajeCamuflado,
        boolean puntajeDisponible
) {
}
