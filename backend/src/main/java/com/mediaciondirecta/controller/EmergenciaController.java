package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.ActualizarUbicacionRequest;
import com.mediaciondirecta.dto.CrearEmergenciaRequest;
import com.mediaciondirecta.dto.EmergenciaResponse;
import com.mediaciondirecta.dto.UbicacionResponse;
import com.mediaciondirecta.service.AuthService;
import com.mediaciondirecta.service.EmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/emergencias")
public class EmergenciaController {

    private final AuthService authService;
    private final EmergenciaService emergenciaService;

    public EmergenciaController(AuthService authService, EmergenciaService emergenciaService) {
        this.authService = authService;
        this.emergenciaService = emergenciaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmergenciaResponse crearEmergencia(@RequestHeader("Authorization") String authorizationHeader,
                                              @Valid @RequestBody CrearEmergenciaRequest request) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return emergenciaService.crear(usuarioId, request);
    }

    @GetMapping("/mis-emergencias")
    public List<EmergenciaResponse> listarMisEmergencias(@RequestHeader("Authorization") String authorizationHeader) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return emergenciaService.listarMisEmergencias(usuarioId);
    }

    @GetMapping("/{id}")
    public EmergenciaResponse obtenerDetalle(@PathVariable Long id) {
        return emergenciaService.obtenerDetalle(id);
    }

    @PostMapping("/{id}/ubicacion")
    @ResponseStatus(HttpStatus.CREATED)
    public UbicacionResponse actualizarUbicacion(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody ActualizarUbicacionRequest request) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return emergenciaService.actualizarUbicacion(usuarioId, id, request);
    }

    @PatchMapping("/{id}/cancelar")
    public EmergenciaResponse cancelar(@RequestHeader("Authorization") String authorizationHeader,
                                       @PathVariable Long id) {
        Long usuarioId = authService.requerirUsuarioId(authorizationHeader);
        return emergenciaService.cancelar(usuarioId, id);
    }
}
