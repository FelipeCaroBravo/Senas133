package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.AsignarInterpreteRequest;
import com.mediaciondirecta.dto.SolicitudInterpreteResponse;
import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.entity.SolicitudInterprete;
import com.mediaciondirecta.enums.EstadoSolicitudInterprete;
import com.mediaciondirecta.repository.SolicitudInterpreteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InterpreteService {

    private final EmergenciaService emergenciaService;
    private final SolicitudInterpreteRepository solicitudRepository;
    private final AlertStreamService alertStreamService;

    public InterpreteService(EmergenciaService emergenciaService,
                             SolicitudInterpreteRepository solicitudRepository,
                             AlertStreamService alertStreamService) {
        this.emergenciaService = emergenciaService;
        this.solicitudRepository = solicitudRepository;
        this.alertStreamService = alertStreamService;
    }

    @Transactional
    public SolicitudInterpreteResponse solicitar(Long emergenciaId) {
        emergenciaService.marcarRequiereInterprete(emergenciaId);
        SolicitudInterprete solicitud = solicitudRepository.findByEmergenciaId(emergenciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud de intérprete no encontrada"));
        return SolicitudInterpreteResponse.from(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudInterpreteResponse> listarPendientes() {
        return solicitudRepository.findByEstadoInOrderByCreadoEnAsc(List.of(
                        EstadoSolicitudInterprete.SOLICITADO,
                        EstadoSolicitudInterprete.ASIGNADO,
                        EstadoSolicitudInterprete.EN_LLAMADA
                ))
                .stream()
                .map(SolicitudInterpreteResponse::from)
                .toList();
    }

    @Transactional
    public SolicitudInterpreteResponse asignar(Long emergenciaId, AsignarInterpreteRequest request) {
        SolicitudInterprete solicitud = solicitudRepository.findByEmergenciaId(emergenciaId)
                .orElseGet(() -> crearSolicitud(emergenciaId));
        solicitud.setNombreInterprete(request.nombreInterprete());
        solicitud.setUrlSalaVideo(request.urlSalaVideo());
        solicitud.setEstado(EstadoSolicitudInterprete.ASIGNADO);

        SolicitudInterpreteResponse response = SolicitudInterpreteResponse.from(solicitud);
        alertStreamService.emitir("interprete_asignado", response);
        return response;
    }

    @Transactional
    public SolicitudInterpreteResponse iniciarLlamada(Long emergenciaId) {
        SolicitudInterprete solicitud = solicitudRepository.findByEmergenciaId(emergenciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud de intérprete no encontrada"));
        solicitud.setEstado(EstadoSolicitudInterprete.EN_LLAMADA);
        SolicitudInterpreteResponse response = SolicitudInterpreteResponse.from(solicitud);
        alertStreamService.emitir("videollamada_iniciada", response);
        return response;
    }

    private SolicitudInterprete crearSolicitud(Long emergenciaId) {
        Emergencia emergencia = emergenciaService.obtenerEntidad(emergenciaId);
        emergencia.setRequiereInterprete(true);
        SolicitudInterprete solicitud = new SolicitudInterprete();
        solicitud.setEmergencia(emergencia);
        solicitud.setEstado(EstadoSolicitudInterprete.SOLICITADO);
        return solicitudRepository.save(solicitud);
    }
}
