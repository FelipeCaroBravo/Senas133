package com.mediaciondirecta.dto;

public record ClaveUnicaMockRequest(
        String rut,
        String nombreCompleto,
        String numeroDocumento,
        String codigoDispositivo
) {
}
