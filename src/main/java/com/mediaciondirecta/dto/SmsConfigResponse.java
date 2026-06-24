package com.mediaciondirecta.dto;

public record SmsConfigResponse(
        boolean habilitado,
        String numeroDestino,
        String plantilla,
        String notaUso
) {
}
