package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.CamuflajeConfigResponse;
import com.mediaciondirecta.dto.CamuflajeEmergenciaResponse;
import com.mediaciondirecta.dto.CamuflajeResultadoResponse;
import com.mediaciondirecta.dto.CrearEmergenciaCamufladaRequest;
import com.mediaciondirecta.dto.CrearEmergenciaRequest;
import com.mediaciondirecta.dto.EmergenciaResponse;
import com.mediaciondirecta.dto.GuardarRespuestasCamuflajeRequest;
import com.mediaciondirecta.dto.RespuestaContextoResponse;
import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.entity.RespuestaContextoEmergencia;
import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.EstadoEmergencia;
import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;
import com.mediaciondirecta.repository.RespuestaContextoEmergenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CamuflajeService {

    private final EmergenciaService emergenciaService;
    private final RespuestaContextoEmergenciaRepository respuestaRepository;
    private final AlertStreamService alertStreamService;

    public CamuflajeService(
            EmergenciaService emergenciaService,
            RespuestaContextoEmergenciaRepository respuestaRepository,
            AlertStreamService alertStreamService
    ) {
        this.emergenciaService = emergenciaService;
        this.respuestaRepository = respuestaRepository;
        this.alertStreamService = alertStreamService;
    }

    public CamuflajeConfigResponse obtenerConfiguracion() {
        return new CamuflajeConfigResponse(
                "Modo Camuflaje",
                "Puzzle/Rompecabezas",
                "fantasma",
                3,
                3,
                "Calculando puntaje...",
                List.of(
                        new CamuflajeConfigResponse.PreguntaCamuflaje(
                                "AGRESOR_CERCA",
                                "¿El agresor está cerca?",
                                "El agresor está cerca",
                                1
                        ),
                        new CamuflajeConfigResponse.PreguntaCamuflaje(
                                "HAY_ARMA",
                                "¿El agresor tiene un arma u objeto peligroso?",
                                "El agresor tiene un arma u objeto peligroso",
                                2
                        ),
                        new CamuflajeConfigResponse.PreguntaCamuflaje(
                                "NECESITA_AYUDA_INMEDIATA",
                                "¿Necesitas ayuda inmediata?",
                                "La víctima necesita ayuda inmediata",
                                3
                        ),
                        new CamuflajeConfigResponse.PreguntaCamuflaje(
                                "MENORES_PRESENTES",
                                "¿Hay niños, niñas o personas vulnerables presentes?",
                                "Hay menores o personas vulnerables presentes",
                                4
                        ),
                        new CamuflajeConfigResponse.PreguntaCamuflaje(
                                "SALIDA_BLOQUEADA",
                                "¿La salida está bloqueada?",
                                "La salida está bloqueada",
                                5
                        )
                )
        );
    }

    /**
     * Flujo actualizado:
     * Inicio -> Modo camuflaje -> Puzzle -> preguntas literales -> se crea/envía la alerta
     * -> pantalla "Calculando puntaje..." -> CENCO confirma -> se muestra puntaje.
     */
    @Transactional
    public CamuflajeEmergenciaResponse crearEmergenciaCamuflada(CrearEmergenciaCamufladaRequest request) {
        CrearEmergenciaRequest camuflada = new CrearEmergenciaRequest(
                request.usuarioId(),
                TipoEmergencia.DELITOS_GRAVES,
                SubtipoIncidente.VIOLENCIA_INTRAFAMILIAR,
                "Alerta camuflada enviada desde puzzle",
                "CAMUFLAJE_PUZZLE",
                true,
                true,
                CanalAlerta.INTERNET,
                request.latitud(),
                request.longitud(),
                request.precisionMetros()
        );

        EmergenciaResponse emergencia = emergenciaService.crear(request.usuarioId(), camuflada);

        guardarRespuestas(
                emergencia.id(),
                new GuardarRespuestasCamuflajeRequest(request.respuestas())
        );

        return new CamuflajeEmergenciaResponse(
                emergencia.id(),
                emergencia.estado(),
                "Calculando puntaje...",
                false
        );
    }

    /**
     * Endpoint antiguo conservado por compatibilidad:
     * permite crear alerta camuflada sin enviar respuestas.
     */
    @Transactional
    public EmergenciaResponse crearEmergenciaCamufladaCompat(Long usuarioId, CrearEmergenciaRequest request) {
        CrearEmergenciaRequest camuflada = new CrearEmergenciaRequest(
                usuarioId,
                request.tipo() != null ? request.tipo() : TipoEmergencia.DELITOS_GRAVES,
                request.subtipo() != null ? request.subtipo() : SubtipoIncidente.VIOLENCIA_INTRAFAMILIAR,
                request.mensaje() != null ? request.mensaje() : "Alerta iniciada desde modo camuflaje",
                request.fraseCodigo() != null ? request.fraseCodigo() : "CAMUFLAJE_PUZZLE",
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
    public List<RespuestaContextoResponse> guardarRespuestas(
            Long emergenciaId,
            GuardarRespuestasCamuflajeRequest request
    ) {
        Emergencia emergencia = emergenciaService.obtenerEntidad(emergenciaId);

        Map<String, CamuflajeConfigResponse.PreguntaCamuflaje> preguntas = obtenerConfiguracion()
                .preguntas()
                .stream()
                .collect(Collectors.toMap(
                        CamuflajeConfigResponse.PreguntaCamuflaje::codigo,
                        pregunta -> pregunta
                ));

        List<RespuestaContextoEmergencia> entidades = request.respuestas()
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(item ->
                        item.orden() == null
                                ? obtenerOrden(preguntas, item.codigoPregunta())
                                : item.orden()
                ))
                .map(item -> {
                    CamuflajeConfigResponse.PreguntaCamuflaje config =
                            preguntas.get(item.codigoPregunta());

                    RespuestaContextoEmergencia respuesta = new RespuestaContextoEmergencia();
                    respuesta.setEmergencia(emergencia);
                    respuesta.setCodigoPregunta(item.codigoPregunta());

                    respuesta.setTextoPregunta(
                            item.textoPregunta() != null && !item.textoPregunta().isBlank()
                                    ? item.textoPregunta()
                                    : config != null
                                            ? config.textoCamuflado()
                                            : item.codigoPregunta()
                    );

                    respuesta.setRespuesta(item.respuesta());

                    respuesta.setOrden(
                            item.orden() != null
                                    ? item.orden()
                                    : obtenerOrden(preguntas, item.codigoPregunta())
                    );

                    respuesta.setModoCamuflaje(true);

                    return respuesta;
                })
                .toList();

        List<RespuestaContextoResponse> response = respuestaRepository.saveAll(entidades)
                .stream()
                .map(RespuestaContextoResponse::from)
                .toList();

        alertStreamService.emitir("contexto_camuflaje_actualizado", response);

        return response;
    }

    @Transactional(readOnly = true)
    public CamuflajeResultadoResponse obtenerResultado(Long emergenciaId) {
        Emergencia emergencia = emergenciaService.obtenerEntidad(emergenciaId);

        boolean confirmada = estaConfirmadaPorCenco(emergencia.getEstado());
        Integer puntaje = confirmada ? calcularPuntaje(emergencia) : null;

        return new CamuflajeResultadoResponse(
                emergencia.getId(),
                emergencia.getEstado(),
                confirmada,
                puntaje,
                confirmada ? "¡Nivel completado!" : "Calculando puntaje..."
        );
    }

    @Transactional(readOnly = true)
    public List<RespuestaContextoResponse> listarRespuestas(Long emergenciaId) {
        return respuestaRepository.findByEmergenciaIdOrderByOrdenAsc(emergenciaId)
                .stream()
                .map(RespuestaContextoResponse::from)
                .toList();
    }

    private int obtenerOrden(
            Map<String, CamuflajeConfigResponse.PreguntaCamuflaje> preguntas,
            String codigo
    ) {
        CamuflajeConfigResponse.PreguntaCamuflaje pregunta = preguntas.get(codigo);
        return pregunta != null ? pregunta.orden() : 999;
    }

    private boolean estaConfirmadaPorCenco(EstadoEmergencia estado) {
        return estado == EstadoEmergencia.RECIBIDA
                || estado == EstadoEmergencia.EN_ATENCION
                || estado == EstadoEmergencia.PATRULLA_DESPACHADA
                || estado == EstadoEmergencia.INTERPRETE_SOLICITADO
                || estado == EstadoEmergencia.INTERPRETE_CONECTADO
                || estado == EstadoEmergencia.CERRADA;
    }

    private int calcularPuntaje(Emergencia emergencia) {
        List<RespuestaContextoEmergencia> respuestas =
                respuestaRepository.findByEmergenciaIdOrderByOrdenAsc(emergencia.getId());

        long afirmativas = respuestas.stream()
                .filter(respuesta -> Boolean.TRUE.equals(respuesta.getRespuesta()))
                .count();

        int precisionBonus = emergencia.getPrecisionMetros() == null
                ? 0
                : Math.max(0, 100 - emergencia.getPrecisionMetros().intValue());

        return 10000
                + Math.toIntExact(afirmativas * 750)
                + precisionBonus
                + emergencia.getId().intValue();
    }
}