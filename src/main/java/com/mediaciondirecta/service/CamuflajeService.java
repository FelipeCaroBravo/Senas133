package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.CamuflajeConfigResponse;
import com.mediaciondirecta.dto.CrearEmergenciaRequest;
import com.mediaciondirecta.dto.EmergenciaResponse;
import com.mediaciondirecta.dto.GuardarRespuestasCamuflajeRequest;
import com.mediaciondirecta.dto.RespuestaContextoResponse;
import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.entity.RespuestaContextoEmergencia;
import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;
import com.mediaciondirecta.repository.RespuestaContextoEmergenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class CamuflajeService {

    private final EmergenciaService emergenciaService;
    private final RespuestaContextoEmergenciaRepository respuestaRepository;
    private final AlertStreamService alertStreamService;

    public CamuflajeService(EmergenciaService emergenciaService,
                            RespuestaContextoEmergenciaRepository respuestaRepository,
                            AlertStreamService alertStreamService) {
        this.emergenciaService = emergenciaService;
        this.respuestaRepository = respuestaRepository;
        this.alertStreamService = alertStreamService;
    }

    public CamuflajeConfigResponse obtenerConfiguracion() {
        return new CamuflajeConfigResponse(
                "Modo Seguro",
                "Puzzle/Rompecabezas",
                "fantasma",
                3,
                3,
                "Cargando puntuación...",
                List.of(
                        new CamuflajeConfigResponse.PreguntaCamuflaje("AGRESOR_ARMADO", "¿Tiene objeto peligroso?", "El agresor está armado", 1),
                        new CamuflajeConfigResponse.PreguntaCamuflaje("AGRESOR_PRESENTE", "¿Está en la misma sala?", "El agresor sigue presente", 2),
                        new CamuflajeConfigResponse.PreguntaCamuflaje("VICTIMA_HERIDA", "¿Te duele algo?", "La víctima está herida", 3),
                        new CamuflajeConfigResponse.PreguntaCamuflaje("MENORES_PRESENTES", "¿Hay niños cerca?", "Hay menores de edad presentes", 4),
                        new CamuflajeConfigResponse.PreguntaCamuflaje("SALIDA_BLOQUEADA", "¿La puerta está cerrada?", "La salida está bloqueada", 5)
                )
        );
    }

    @Transactional
    public EmergenciaResponse crearEmergenciaCamuflada(Long usuarioId, CrearEmergenciaRequest request) {
        CrearEmergenciaRequest camuflada = new CrearEmergenciaRequest(
                request.tipo() != null ? request.tipo() : TipoEmergencia.DELITOS_GRAVES,
                request.subtipo() != null ? request.subtipo() : SubtipoIncidente.VIOLENCIA_INTRAFAMILIAR,
                request.mensaje() != null ? request.mensaje() : "Alerta iniciada desde modo camuflaje",
                request.fraseCodigo(),
                true,
                request.requiereInterprete(),
                request.canal() != null ? request.canal() : CanalAlerta.INTERNET,
                request.latitud(),
                request.longitud(),
                request.precisionMetros()
        );
        return emergenciaService.crear(usuarioId, camuflada);
    }

    @Transactional
    public List<RespuestaContextoResponse> guardarRespuestas(Long emergenciaId, GuardarRespuestasCamuflajeRequest request) {
        Emergencia emergencia = emergenciaService.obtenerEntidad(emergenciaId);

        List<RespuestaContextoEmergencia> entidades = request.respuestas().stream()
                .sorted(Comparator.comparing(item -> item.orden() == null ? 0 : item.orden()))
                .map(item -> {
                    RespuestaContextoEmergencia respuesta = new RespuestaContextoEmergencia();
                    respuesta.setEmergencia(emergencia);
                    respuesta.setCodigoPregunta(item.codigoPregunta());
                    respuesta.setTextoPregunta(item.textoPregunta());
                    respuesta.setRespuesta(item.respuesta());
                    respuesta.setOrden(item.orden());
                    respuesta.setModoCamuflaje(true);
                    return respuesta;
                })
                .toList();

        List<RespuestaContextoResponse> response = respuestaRepository.saveAll(entidades).stream()
                .map(RespuestaContextoResponse::from)
                .toList();

        alertStreamService.emitir("contexto_camuflaje_actualizado", response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<RespuestaContextoResponse> listarRespuestas(Long emergenciaId) {
        return respuestaRepository.findByEmergenciaIdOrderByOrdenAsc(emergenciaId)
                .stream()
                .map(RespuestaContextoResponse::from)
                .toList();
    }
}
