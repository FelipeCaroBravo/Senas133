package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.CambiarEstadoRequest;
import com.mediaciondirecta.dto.EmergenciaResponse;
import com.mediaciondirecta.dto.UbicacionResponse;
import com.mediaciondirecta.dto.RespuestaContextoResponse;
import com.mediaciondirecta.service.CamuflajeService;
import com.mediaciondirecta.service.AlertStreamService;
import com.mediaciondirecta.service.EmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/central")
public class CentralController {

    private final EmergenciaService emergenciaService;
    private final AlertStreamService alertStreamService;
    private final CamuflajeService camuflajeService;

    public CentralController(EmergenciaService emergenciaService, AlertStreamService alertStreamService, CamuflajeService camuflajeService) {
        this.emergenciaService = emergenciaService;
        this.alertStreamService = alertStreamService;
        this.camuflajeService = camuflajeService;
    }

    // Para prototipo: representa el panel de la central.
    @GetMapping("/emergencias/activas")
    public List<EmergenciaResponse> listarEmergenciasActivas() {
        return emergenciaService.listarActivasParaCentral();
    }

    @PatchMapping("/emergencias/{id}/estado")
    public EmergenciaResponse cambiarEstado(@PathVariable Long id,
                                            @Valid @RequestBody CambiarEstadoRequest request) {
        return emergenciaService.cambiarEstadoComoCentral(id, request);
    }

    @GetMapping("/emergencias/{id}/ubicaciones")
    public List<UbicacionResponse> listarUbicaciones(@PathVariable Long id) {
        return emergenciaService.listarUbicaciones(id);
    }

    @GetMapping("/emergencias/{id}/contexto")
    public List<RespuestaContextoResponse> listarContextoCamuflaje(@PathVariable Long id) {
        return camuflajeService.listarRespuestas(id);
    }

    // Flujo en vivo para que el panel reciba nuevas alertas o cambios de ubicación.
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return alertStreamService.crearConexion();
    }
}
