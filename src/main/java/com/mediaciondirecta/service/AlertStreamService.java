package com.mediaciondirecta.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AlertStreamService {

    private final List<SseEmitter> emisores = new CopyOnWriteArrayList<>();

    public SseEmitter crearConexion() {
        SseEmitter emitter = new SseEmitter(0L);
        emisores.add(emitter);

        emitter.onCompletion(() -> emisores.remove(emitter));
        emitter.onTimeout(() -> emisores.remove(emitter));
        emitter.onError(error -> emisores.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("conectado")
                    .data("Central conectada al flujo de emergencias"));
        } catch (IOException e) {
            emisores.remove(emitter);
        }

        return emitter;
    }

    public void emitir(String nombreEvento, Object data) {
        for (SseEmitter emitter : emisores) {
            try {
                emitter.send(SseEmitter.event().name(nombreEvento).data(data));
            } catch (IOException e) {
                emisores.remove(emitter);
            }
        }
    }
}
