package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.PrepararSmsRequest;
import com.mediaciondirecta.dto.PrepararSmsResponse;
import com.mediaciondirecta.dto.SincronizarSmsRequest;
import com.mediaciondirecta.dto.SmsConfigResponse;
import com.mediaciondirecta.dto.SmsContingenciaResponse;
import com.mediaciondirecta.service.AuthService;
import com.mediaciondirecta.service.SmsFallbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
public class SmsFallbackController {

    private final AuthService authService;
    private final SmsFallbackService smsFallbackService;

    public SmsFallbackController(AuthService authService, SmsFallbackService smsFallbackService) {
        this.authService = authService;
        this.smsFallbackService = smsFallbackService;
    }

    @GetMapping("/configuracion")
    public SmsConfigResponse obtenerConfiguracion() {
        return smsFallbackService.obtenerConfiguracion();
    }

    @PostMapping("/preparar")
    public PrepararSmsResponse prepararSms(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                           @Valid @RequestBody PrepararSmsRequest request) {
        Long usuarioId = authService.resolverUsuarioId(authorizationHeader, request.usuarioId());
        return smsFallbackService.prepararSms(usuarioId, request);
    }

    @PostMapping("/sincronizar")
    @ResponseStatus(HttpStatus.CREATED)
    public SmsContingenciaResponse sincronizarSms(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                  @Valid @RequestBody SincronizarSmsRequest request) {
        Long usuarioId = authService.resolverUsuarioId(authorizationHeader, request.usuarioId());
        return smsFallbackService.sincronizar(usuarioId, request);
    }

    @GetMapping("/mis-sms")
    public List<SmsContingenciaResponse> listarMisSms(@RequestHeader("Authorization") String authorizationHeader) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return smsFallbackService.listarMisSms(usuarioId);
    }
}
