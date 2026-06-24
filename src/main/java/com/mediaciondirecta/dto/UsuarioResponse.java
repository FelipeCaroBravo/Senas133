package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nombreCompleto,
        String rut,
        String telefono,
        boolean verificado
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getRut(),
                usuario.getTelefono(),
                usuario.isVerificado()
        );
    }
}
