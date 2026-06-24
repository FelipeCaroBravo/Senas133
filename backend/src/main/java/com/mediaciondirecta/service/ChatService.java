package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.MensajeChatRequest;
import com.mediaciondirecta.dto.MensajeChatResponse;
import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.entity.MensajeChatEmergencia;
import com.mediaciondirecta.repository.MensajeChatEmergenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final EmergenciaService emergenciaService;
    private final MensajeChatEmergenciaRepository mensajeRepository;
    private final AlertStreamService alertStreamService;

    public ChatService(EmergenciaService emergenciaService,
                       MensajeChatEmergenciaRepository mensajeRepository,
                       AlertStreamService alertStreamService) {
        this.emergenciaService = emergenciaService;
        this.mensajeRepository = mensajeRepository;
        this.alertStreamService = alertStreamService;
    }

    @Transactional
    public MensajeChatResponse enviarMensaje(Long emergenciaId, MensajeChatRequest request) {
        Emergencia emergencia = emergenciaService.obtenerEntidad(emergenciaId);
        MensajeChatEmergencia mensaje = new MensajeChatEmergencia();
        mensaje.setEmergencia(emergencia);
        mensaje.setRolEmisor(request.rolEmisor());
        mensaje.setMensaje(request.mensaje().trim());

        MensajeChatResponse response = MensajeChatResponse.from(mensajeRepository.save(mensaje));
        alertStreamService.emitir("mensaje_chat", response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<MensajeChatResponse> listarMensajes(Long emergenciaId) {
        return mensajeRepository.findByEmergenciaIdOrderByCreadoEnAsc(emergenciaId)
                .stream()
                .map(MensajeChatResponse::from)
                .toList();
    }
}
