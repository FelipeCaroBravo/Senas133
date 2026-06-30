package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.SmsContingencia;
import com.mediaciondirecta.enums.EstadoSmsContingencia;
import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;

import java.time.LocalDateTime;

public record SmsContingenciaResponse(
        Long id,
        Long usuarioId,
        Long emergenciaId,
        TipoEmergencia tipo,
        SubtipoIncidente subtipo,
        EstadoSmsContingencia estado,
        String numeroDestino,
        String textoSms,
        boolean enviadoPorUsuario,
        boolean sincronizado,
        LocalDateTime creadoEn
) {
    public static SmsContingenciaResponse from(SmsContingencia sms) {
        return new SmsContingenciaResponse(
                sms.getId(),
                sms.getUsuario().getId(),
                sms.getEmergencia() != null ? sms.getEmergencia().getId() : null,
                sms.getTipo(),
                sms.getSubtipo(),
                sms.getEstado(),
                sms.getNumeroDestino(),
                sms.getTextoSms(),
                sms.isEnviadoPorUsuario(),
                sms.isSincronizado(),
                sms.getCreadoEn()
        );
    }
}
