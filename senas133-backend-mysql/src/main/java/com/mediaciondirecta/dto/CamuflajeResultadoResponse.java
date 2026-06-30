package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.EstadoEmergencia;

public record CamuflajeResultadoResponse(
        Long emergenciaId,
        EstadoEmergencia estadoEmergencia,
        boolean confirmadaPorCenco,
        Integer puntaje,
        String mensajePantalla
) {
}
