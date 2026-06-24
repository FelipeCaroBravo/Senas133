package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.CrearEmergenciaRequest;
import com.mediaciondirecta.dto.PrepararSmsRequest;
import com.mediaciondirecta.dto.PrepararSmsResponse;
import com.mediaciondirecta.dto.SincronizarSmsRequest;
import com.mediaciondirecta.dto.SmsConfigResponse;
import com.mediaciondirecta.dto.SmsContingenciaResponse;
import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.entity.SmsContingencia;
import com.mediaciondirecta.entity.Usuario;
import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.EstadoSmsContingencia;
import com.mediaciondirecta.repository.SmsContingenciaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SmsFallbackService {

    private final UsuarioService usuarioService;
    private final EmergenciaService emergenciaService;
    private final SmsContingenciaRepository smsRepository;

    @Value("${app.sms.enabled:true}")
    private boolean smsEnabled;

    @Value("${app.sms.center-phone:+56900000000}")
    private String centerPhone;

    public SmsFallbackService(UsuarioService usuarioService,
                              EmergenciaService emergenciaService,
                              SmsContingenciaRepository smsRepository) {
        this.usuarioService = usuarioService;
        this.emergenciaService = emergenciaService;
        this.smsRepository = smsRepository;
    }

    public SmsConfigResponse obtenerConfiguracion() {
        return new SmsConfigResponse(
                smsEnabled,
                centerPhone,
                "SEÑAS133 ID:{usuarioId} T:{tipo} ST:{subtipo} GPS:{lat},{lon} P:{precision} MSG:{mensaje}",
                "El backend solo prepara el SMS. Si no hay internet, la app del teléfono debe abrir Mensajes y el usuario confirma el envío."
        );
    }

    public PrepararSmsResponse prepararSms(Long usuarioId, PrepararSmsRequest request) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId);
        String texto = construirTextoSms(usuario, request);
        return new PrepararSmsResponse(
                centerPhone,
                texto,
                texto.length(),
                true,
                "Abrir la aplicación de SMS del teléfono con este número y texto precargado."
        );
    }

    @Transactional
    public SmsContingenciaResponse sincronizar(Long usuarioId, SincronizarSmsRequest request) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId);

        Emergencia emergencia = null;
        if (request.crearEmergenciaEnServidor()) {
            CrearEmergenciaRequest crearEmergenciaRequest = new CrearEmergenciaRequest(
                    request.tipo(),
                    request.subtipo(),
                    "Emergencia sincronizada desde SMS de contingencia",
                    null,
                    request.modoCamuflaje(),
                    request.requiereInterprete(),
                    CanalAlerta.SMS_CONTINGENCIA,
                    request.latitud(),
                    request.longitud(),
                    request.precisionMetros()
            );
            emergencia = toEntity(emergenciaService.crear(usuarioId, crearEmergenciaRequest).id());
        }

        SmsContingencia sms = new SmsContingencia();
        sms.setUsuario(usuario);
        sms.setEmergencia(emergencia);
        sms.setTipo(request.tipo());
        sms.setSubtipo(request.subtipo());
        sms.setEstado(EstadoSmsContingencia.SINCRONIZADO);
        sms.setNumeroDestino(centerPhone);
        sms.setTextoSms(request.textoSms());
        sms.setLatitud(request.latitud());
        sms.setLongitud(request.longitud());
        sms.setPrecisionMetros(request.precisionMetros());
        sms.setEnviadoPorUsuario(request.enviadoPorUsuario());
        sms.setSincronizado(true);
        sms.setCodigoDispositivo(request.codigoDispositivo());
        sms.setGeneradoEnDispositivo(request.generadoEnDispositivo());

        return SmsContingenciaResponse.from(smsRepository.save(sms));
    }

    @Transactional(readOnly = true)
    public List<SmsContingenciaResponse> listarMisSms(Long usuarioId) {
        return smsRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream()
                .map(SmsContingenciaResponse::from)
                .toList();
    }

    private Emergencia toEntity(Long emergenciaId) {
        return emergenciaService.obtenerEntidad(emergenciaId);
    }

    private String construirTextoSms(Usuario usuario, PrepararSmsRequest request) {
        String gps = request.latitud() != null && request.longitud() != null
                ? request.latitud() + "," + request.longitud()
                : "SIN_GPS";

        String mensaje = request.mensajeCorto() != null && !request.mensajeCorto().isBlank()
                ? request.mensajeCorto().trim()
                : "ALERTA";

        return "SEÑAS133 "
                + "ID:" + usuario.getId()
                + " T:" + request.tipo()
                + " ST:" + (request.subtipo() != null ? request.subtipo() : "NO_INDICA")
                + " GPS:" + gps
                + " P:" + (request.precisionMetros() != null ? request.precisionMetros() + "m" : "ND")
                + " CAM:" + (request.modoCamuflaje() ? "SI" : "NO")
                + " MSG:" + mensaje;
    }
}
