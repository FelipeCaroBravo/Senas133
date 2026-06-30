package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SincronizarSmsRequest(
        Long usuarioId,
        @NotNull TipoEmergencia tipo,
        SubtipoIncidente subtipo,
        Double latitud,
        Double longitud,
        Double precisionMetros,
        @NotBlank String textoSms,
        String codigoDispositivo,
        LocalDateTime generadoEnDispositivo,
        boolean enviadoPorUsuario,
        boolean crearEmergenciaEnServidor,
        boolean modoCamuflaje,
        boolean requiereInterprete
) {
}
