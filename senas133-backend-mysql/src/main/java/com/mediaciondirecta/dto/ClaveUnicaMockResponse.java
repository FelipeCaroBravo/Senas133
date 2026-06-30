package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.Usuario;

public record ClaveUnicaMockResponse(
        Long usuarioId,
        String rut,
        String nombreCompleto,
        boolean claveUnicaValidada,
        boolean perfilCompleto
) {
    public static ClaveUnicaMockResponse from(Usuario usuario) {
        return new ClaveUnicaMockResponse(
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombreCompleto(),
                usuario.isClaveUnicaValidada(),
                usuario.isPerfilCompleto()
        );
    }
}
