package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.CamuflajeConfigResponse;
import com.mediaciondirecta.dto.CamuflajeEmergenciaResponse;
import com.mediaciondirecta.dto.CamuflajeResultadoResponse;
import com.mediaciondirecta.dto.CrearEmergenciaCamufladaRequest;
import com.mediaciondirecta.dto.CrearEmergenciaRequest;
import com.mediaciondirecta.dto.EmergenciaResponse;
import com.mediaciondirecta.dto.GuardarRespuestasCamuflajeRequest;
import com.mediaciondirecta.dto.RespuestaContextoResponse;
import com.mediaciondirecta.service.AuthService;
import com.mediaciondirecta.service.CamuflajeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/camuflaje")
public class CamuflajeController {

    private final AuthService authService;
    private final CamuflajeService camuflajeService;

    public CamuflajeController(AuthService authService, CamuflajeService camuflajeService) {
        this.authService = authService;
        this.camuflajeService = camuflajeService;
    }

    @GetMapping("/configuracion")
    public CamuflajeConfigResponse obtenerConfiguracion() {
        return camuflajeService.obtenerConfiguracion();
    }

    // Flujo nuevo: el frontend envía respuestas ocultas; el backend crea la alerta y queda esperando confirmación de CENCO.
    @PostMapping("/emergencias")
    @ResponseStatus(HttpStatus.CREATED)
    public CamuflajeEmergenciaResponse crearEmergenciaCamuflada(@Valid @RequestBody CrearEmergenciaCamufladaRequest request) {
        return camuflajeService.crearEmergenciaCamuflada(request);
    }

    @GetMapping("/emergencias/{id}/resultado")
    public CamuflajeResultadoResponse obtenerResultado(@PathVariable Long id) {
        return camuflajeService.obtenerResultado(id);
    }

    // Endpoint antiguo opcional, útil si se prueba con token y sin respuestas del puzzle.
    @PostMapping("/emergencias/compat")
    @ResponseStatus(HttpStatus.CREATED)
    public EmergenciaResponse crearEmergenciaCamufladaCompat(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                             @Valid @RequestBody CrearEmergenciaRequest request) {
        Long usuarioId = authService.resolverUsuarioId(authorizationHeader, request.usuarioId());
        return camuflajeService.crearEmergenciaCamufladaCompat(usuarioId, request);
    }

    @PostMapping("/emergencias/{id}/respuestas")
    @ResponseStatus(HttpStatus.CREATED)
    public List<RespuestaContextoResponse> guardarRespuestas(@PathVariable Long id,
                                                             @Valid @RequestBody GuardarRespuestasCamuflajeRequest request) {
        return camuflajeService.guardarRespuestas(id, request);
    }
}
