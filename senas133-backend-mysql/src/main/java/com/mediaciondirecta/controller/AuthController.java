package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.ClaveUnicaMockRequest;
import com.mediaciondirecta.dto.ClaveUnicaMockResponse;
import com.mediaciondirecta.dto.LoginPinRequest;
import com.mediaciondirecta.dto.LoginResponse;
import com.mediaciondirecta.dto.RegistroRequest;
import com.mediaciondirecta.dto.UsuarioResponse;
import com.mediaciondirecta.entity.Usuario;
import com.mediaciondirecta.service.AuthService;
import com.mediaciondirecta.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    public AuthController(UsuarioService usuarioService, AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return usuarioService.registrar(request);
    }

    // Prototipo: simula el retorno exitoso de Clave Única.
    // El frontend móvil actual llama a este endpoint sin parámetros.
    @PostMapping("/clave-unica/mock")
    public ClaveUnicaMockResponse loginClaveUnicaMock(@RequestBody(required = false) ClaveUnicaMockRequest request) {
        Usuario usuario = usuarioService.validarClaveUnicaMock(request != null ? request : new ClaveUnicaMockRequest(null, null, null, null));
        authService.crearSesionParaUsuario(usuario);
        return ClaveUnicaMockResponse.from(usuario);
    }

    // Compatible con el frontend móvil: devuelve el usuario directo y no exige que el frontend maneje token.
    @PostMapping("/login-pin")
    public UsuarioResponse loginConPin(@Valid @RequestBody LoginPinRequest request) {
        return authService.loginConPinUsuario(request);
    }

    // Endpoint opcional para probar con token desde requests.http/Postman.
    @PostMapping("/login-pin-token")
    public LoginResponse loginConPinToken(@Valid @RequestBody LoginPinRequest request) {
        return authService.loginConPinToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
    }
}
