package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.AsignarInterpreteRequest;
import com.mediaciondirecta.dto.SolicitudInterpreteResponse;
import com.mediaciondirecta.service.InterpreteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InterpreteController {

    private final InterpreteService interpreteService;

    public InterpreteController(InterpreteService interpreteService) {
        this.interpreteService = interpreteService;
    }

    @PostMapping("/emergencias/{id}/interprete/solicitar")
    public SolicitudInterpreteResponse solicitar(@PathVariable Long id) {
        return interpreteService.solicitar(id);
    }

    @GetMapping("/central/interpretes/pendientes")
    public List<SolicitudInterpreteResponse> listarPendientes() {
        return interpreteService.listarPendientes();
    }

    @PatchMapping("/central/emergencias/{id}/interprete/asignar")
    public SolicitudInterpreteResponse asignar(@PathVariable Long id,
                                               @Valid @RequestBody AsignarInterpreteRequest request) {
        return interpreteService.asignar(id, request);
    }

    @PatchMapping("/central/emergencias/{id}/interprete/iniciar-llamada")
    public SolicitudInterpreteResponse iniciarLlamada(@PathVariable Long id) {
        return interpreteService.iniciarLlamada(id);
    }
}
