package com.mediaciondirecta.dto;

public record PrepararSmsResponse(
        String numeroDestino,
        String textoSms,
        int largo,
        boolean requiereConfirmacionUsuario,
        String instruccionApp
) {
}
