package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.ActualizarUbicacionRequest;
import com.mediaciondirecta.dto.CambiarEstadoRequest;
import com.mediaciondirecta.dto.CrearEmergenciaRequest;
import com.mediaciondirecta.dto.EmergenciaResponse;
import com.mediaciondirecta.dto.UbicacionResponse;
import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.entity.SolicitudInterprete;
import com.mediaciondirecta.entity.UbicacionEmergencia;
import com.mediaciondirecta.entity.Usuario;
import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.EstadoEmergencia;
import com.mediaciondirecta.enums.EstadoSolicitudInterprete;
import com.mediaciondirecta.integration.SistemaExternoClient;
import com.mediaciondirecta.repository.EmergenciaRepository;
import com.mediaciondirecta.repository.SolicitudInterpreteRepository;
import com.mediaciondirecta.repository.UbicacionEmergenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmergenciaService {

    private final EmergenciaRepository emergenciaRepository;
    private final UbicacionEmergenciaRepository ubicacionRepository;
    private final SolicitudInterpreteRepository solicitudInterpreteRepository;
    private final UsuarioService usuarioService;
    private final AlertStreamService alertStreamService;
    private final SistemaExternoClient sistemaExternoClient;

    public EmergenciaService(EmergenciaRepository emergenciaRepository,
                             UbicacionEmergenciaRepository ubicacionRepository,
                             SolicitudInterpreteRepository solicitudInterpreteRepository,
                             UsuarioService usuarioService,
                             AlertStreamService alertStreamService,
                             SistemaExternoClient sistemaExternoClient) {
        this.emergenciaRepository = emergenciaRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.solicitudInterpreteRepository = solicitudInterpreteRepository;
        this.usuarioService = usuarioService;
        this.alertStreamService = alertStreamService;
        this.sistemaExternoClient = sistemaExternoClient;
    }

    @Transactional
    public EmergenciaResponse crear(Long usuarioId, CrearEmergenciaRequest request) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId);

        Emergencia emergencia = new Emergencia();
        emergencia.setUsuario(usuario);
        emergencia.setTipo(request.tipo());
        emergencia.setSubtipo(request.subtipo());
        emergencia.setEstado(EstadoEmergencia.ENVIADA);
        emergencia.setCanal(request.canal() != null ? request.canal() : CanalAlerta.INTERNET);
        emergencia.setMensaje(request.mensaje());
        emergencia.setFraseCodigo(request.fraseCodigo());
        emergencia.setModoCamuflaje(request.modoCamuflaje());
        emergencia.setRequiereInterprete(request.requiereInterprete());
        emergencia.setLatitud(request.latitud());
        emergencia.setLongitud(request.longitud());
        emergencia.setPrecisionMetros(request.precisionMetros());

        Emergencia guardada = emergenciaRepository.save(emergencia);

        if (request.latitud() != null && request.longitud() != null) {
            guardarUbicacion(guardada, request.latitud(), request.longitud(), request.precisionMetros(), "INICIAL");
        }

        if (request.requiereInterprete()) {
            crearSolicitudInterpreteSiNoExiste(guardada);
        }

        EmergenciaResponse response = EmergenciaResponse.from(guardada);
        sistemaExternoClient.notificarNuevaEmergencia(response);
        alertStreamService.emitir("nueva_emergencia", response);
        return response;
    }

    @Transactional
    public UbicacionResponse actualizarUbicacion(Long usuarioId, Long emergenciaId, ActualizarUbicacionRequest request) {
        Emergencia emergencia = obtenerEmergencia(emergenciaId);
        validarPropietario(emergencia, usuarioId);
        validarEmergenciaActiva(emergencia);

        emergencia.setLatitud(request.latitud());
        emergencia.setLongitud(request.longitud());
        emergencia.setPrecisionMetros(request.precisionMetros());

        UbicacionEmergencia ubicacion = guardarUbicacion(
                emergencia,
                request.latitud(),
                request.longitud(),
                request.precisionMetros(),
                request.fuente() != null ? request.fuente() : "GPS"
        );

        EmergenciaResponse emergenciaResponse = EmergenciaResponse.from(emergencia);
        UbicacionResponse ubicacionResponse = UbicacionResponse.from(ubicacion);

        alertStreamService.emitir("ubicacion_actualizada", ubicacionResponse);
        alertStreamService.emitir("emergencia_actualizada", emergenciaResponse);

        return ubicacionResponse;
    }

    @Transactional
    public EmergenciaResponse cancelar(Long usuarioId, Long emergenciaId) {
        Emergencia emergencia = obtenerEmergencia(emergenciaId);
        validarPropietario(emergencia, usuarioId);
        validarEmergenciaActiva(emergencia);

        emergencia.setEstado(EstadoEmergencia.CANCELADA);
        emergencia.setCanceladoEn(LocalDateTime.now());

        EmergenciaResponse response = EmergenciaResponse.from(emergencia);
        alertStreamService.emitir("emergencia_cancelada", response);
        return response;
    }

    @Transactional(readOnly = true)
    public EmergenciaResponse obtenerDetalle(Long emergenciaId) {
        return EmergenciaResponse.from(obtenerEmergencia(emergenciaId));
    }

    @Transactional(readOnly = true)
    public Emergencia obtenerEntidad(Long emergenciaId) {
        return obtenerEmergencia(emergenciaId);
    }

    @Transactional(readOnly = true)
    public List<EmergenciaResponse> listarMisEmergencias(Long usuarioId) {
        return emergenciaRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream()
                .map(EmergenciaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmergenciaResponse> listarActivasParaCentral() {
        return emergenciaRepository.findByEstadoInOrderByCreadoEnDesc(List.of(
                        EstadoEmergencia.ACTIVA,
                        EstadoEmergencia.ENVIADA,
                        EstadoEmergencia.RECIBIDA,
                        EstadoEmergencia.EN_ATENCION
                ))
                .stream()
                .map(EmergenciaResponse::from)
                .toList();
    }

    @Transactional
    public EmergenciaResponse cambiarEstadoComoCentral(Long emergenciaId, CambiarEstadoRequest request) {
        Emergencia emergencia = obtenerEmergencia(emergenciaId);
        emergencia.setEstado(request.estado());

        if (request.estado() == EstadoEmergencia.CERRADA) {
            emergencia.setCerradoEn(LocalDateTime.now());
        }

        EmergenciaResponse response = EmergenciaResponse.from(emergencia);
        alertStreamService.emitir("estado_actualizado", response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<UbicacionResponse> listarUbicaciones(Long emergenciaId) {
        if (!emergenciaRepository.existsById(emergenciaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Emergencia no encontrada");
        }

        return ubicacionRepository.findByEmergenciaIdOrderByCreadoEnDesc(emergenciaId)
                .stream()
                .map(UbicacionResponse::from)
                .toList();
    }

    @Transactional
    public void marcarRequiereInterprete(Long emergenciaId) {
        Emergencia emergencia = obtenerEmergencia(emergenciaId);
        emergencia.setRequiereInterprete(true);
        crearSolicitudInterpreteSiNoExiste(emergencia);
        alertStreamService.emitir("interprete_solicitado", EmergenciaResponse.from(emergencia));
    }

    private Emergencia obtenerEmergencia(Long emergenciaId) {
        return emergenciaRepository.findById(emergenciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emergencia no encontrada"));
    }

    private void crearSolicitudInterpreteSiNoExiste(Emergencia emergencia) {
        if (solicitudInterpreteRepository.findByEmergenciaId(emergencia.getId()).isPresent()) {
            return;
        }
        SolicitudInterprete solicitud = new SolicitudInterprete();
        solicitud.setEmergencia(emergencia);
        solicitud.setEstado(EstadoSolicitudInterprete.SOLICITADO);
        solicitudInterpreteRepository.save(solicitud);
    }

    private UbicacionEmergencia guardarUbicacion(Emergencia emergencia,
                                                  Double latitud,
                                                  Double longitud,
                                                  Double precisionMetros,
                                                  String fuente) {
        UbicacionEmergencia ubicacion = new UbicacionEmergencia();
        ubicacion.setEmergencia(emergencia);
        ubicacion.setLatitud(latitud);
        ubicacion.setLongitud(longitud);
        ubicacion.setPrecisionMetros(precisionMetros);
        ubicacion.setFuente(fuente);
        return ubicacionRepository.save(ubicacion);
    }

    private void validarPropietario(Emergencia emergencia, Long usuarioId) {
        if (!emergencia.getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puede modificar una emergencia de otro usuario");
        }
    }

    private void validarEmergenciaActiva(Emergencia emergencia) {
        if (emergencia.getEstado() == EstadoEmergencia.CANCELADA || emergencia.getEstado() == EstadoEmergencia.CERRADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La emergencia ya no está activa");
        }
    }
}
