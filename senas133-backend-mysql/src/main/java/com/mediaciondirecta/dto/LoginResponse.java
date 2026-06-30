package com.mediaciondirecta.dto;

public record LoginResponse(
        String token,
        UsuarioResponse usuario
) {
}
