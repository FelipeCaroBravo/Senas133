package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.LoginPinRequest;
import com.mediaciondirecta.dto.LoginResponse;
import com.mediaciondirecta.dto.RegistroRequest;
import com.mediaciondirecta.dto.UsuarioResponse;
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

    @PostMapping("/login-pin")
    public LoginResponse loginConPin(@Valid @RequestBody LoginPinRequest request) {
        return authService.loginConPin(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
    }
}
