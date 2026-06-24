package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.UsuarioResponse;
import com.mediaciondirecta.service.AuthService;
import com.mediaciondirecta.service.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
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
}
