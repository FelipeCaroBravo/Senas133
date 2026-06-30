package com.mediaciondirecta.dto;

import java.util.List;

public record CamuflajeConfigResponse(
        String nombreModo,
        String apariencia,
        String iconoAcceso,
        int filasPuzzle,
        int columnasPuzzle,
        String mensajeCargaDiscreto,
        List<PreguntaCamuflaje> preguntas
) {
    public record PreguntaCamuflaje(
            String codigo,
            String textoCamuflado,
            String textoReal,
            int orden
    ) {
    }
}
