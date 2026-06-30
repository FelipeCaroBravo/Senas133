package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.ActualizarPerfilRequest;
import com.mediaciondirecta.dto.CambiarPinRequest;
import com.mediaciondirecta.dto.CompletarPerfilRequest;
import com.mediaciondirecta.dto.UsuarioResponse;
import com.mediaciondirecta.service.AuthService;
import com.mediaciondirecta.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public UsuarioController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public UsuarioResponse miPerfil(@RequestHeader("Authorization") String authorizationHeader) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return usuarioService.obtenerPerfil(usuarioId);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtenerPerfilPorId(@PathVariable Long id) {
        return usuarioService.obtenerPerfil(id);
    }

    @PostMapping("/me/completar-perfil")
    public UsuarioResponse completarPerfil(@RequestHeader("Authorization") String authorizationHeader,
                                           @Valid @RequestBody CompletarPerfilRequest request) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return usuarioService.completarPerfil(usuarioId, request);
    }

    @PostMapping("/{id}/completar-perfil")
    public UsuarioResponse completarPerfilPorId(@PathVariable Long id,
                                                @Valid @RequestBody CompletarPerfilRequest request) {
        return usuarioService.completarPerfil(id, request);
    }

    @PatchMapping("/me/perfil")
    public UsuarioResponse actualizarPerfil(@RequestHeader("Authorization") String authorizationHeader,
                                            @Valid @RequestBody ActualizarPerfilRequest request) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return usuarioService.actualizarPerfil(usuarioId, request);
    }

    @PatchMapping("/{id}/perfil")
    public UsuarioResponse actualizarPerfilPorId(@PathVariable Long id,
                                                 @Valid @RequestBody ActualizarPerfilRequest request) {
        return usuarioService.actualizarPerfil(id, request);
    }

    @PatchMapping("/me/pin")
    public UsuarioResponse cambiarPin(@RequestHeader("Authorization") String authorizationHeader,
                                      @Valid @RequestBody CambiarPinRequest request) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return usuarioService.cambiarPin(usuarioId, request);
    }

    @PatchMapping("/{id}/pin")
    public UsuarioResponse cambiarPinPorId(@PathVariable Long id,
                                           @Valid @RequestBody CambiarPinRequest request) {
        return usuarioService.cambiarPin(id, request);
    }
}
