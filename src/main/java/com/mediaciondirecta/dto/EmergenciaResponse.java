package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.EstadoEmergencia;
import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;

import java.time.LocalDateTime;

public record EmergenciaResponse(
        Long id,
        Long usuarioId,
        String nombreUsuario,
        String telefonoUsuario,
        TipoEmergencia tipo,
        SubtipoIncidente subtipo,
        EstadoEmergencia estado,
        CanalAlerta canal,
        String mensaje,
        String fraseCodigo,
        boolean modoCamuflaje,
        boolean requiereInterprete,
        String codigoExterno,
        Double latitud,
        Double longitud,
        Double precisionMetros,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
    public static EmergenciaResponse from(Emergencia emergencia) {
        return new EmergenciaResponse(
                emergencia.getId(),
                emergencia.getUsuario().getId(),
                emergencia.getUsuario().getNombreCompleto(),
                emergencia.getUsuario().getTelefono(),
                emergencia.getTipo(),
                emergencia.getSubtipo(),
                emergencia.getEstado(),
                emergencia.getCanal(),
                emergencia.getMensaje(),
                emergencia.getFraseCodigo(),
                emergencia.isModoCamuflaje(),
                emergencia.isRequiereInterprete(),
                emergencia.getCodigoExterno(),
                emergencia.getLatitud(),
                emergencia.getLongitud(),
                emergencia.getPrecisionMetros(),
                emergencia.getCreadoEn(),
                emergencia.getActualizadoEn()
        );
    }
}
