package com.mediaciondirecta.dto;

import com.mediaciondirecta.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombreCompleto,
        String rut,
        String numeroDocumento,
        String direccionPrincipal,
        String telefono,
        boolean verificado,
        boolean claveUnicaValidada,
        boolean perfilCompleto,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getRut(),
                usuario.getNumeroDocumento(),
                usuario.getDireccionPrincipal(),
                usuario.getTelefono(),
                usuario.isVerificado(),
                usuario.isClaveUnicaValidada(),
                usuario.isPerfilCompleto(),
                usuario.getCreadoEn(),
                usuario.getActualizadoEn()
        );
    }
}
