package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.MensajeChatEmergencia;
import com.mediaciondirecta.enums.RolMensaje;

import java.time.LocalDateTime;

public record MensajeChatResponse(
        Long id,
        Long emergenciaId,
        RolMensaje rolEmisor,
        String mensaje,
        LocalDateTime creadoEn
) {
    public static MensajeChatResponse from(MensajeChatEmergencia mensaje) {
        return new MensajeChatResponse(
                mensaje.getId(),
                mensaje.getEmergencia().getId(),
                mensaje.getRolEmisor(),
                mensaje.getMensaje(),
                mensaje.getCreadoEn()
        );
    }
}
