package com.mediaciondirecta.integration;

import com.mediaciondirecta.dto.EmergenciaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SistemaExternoClient {

    @Value("${app.integration.external-url}")
    private String externalUrl;

    public void notificarNuevaEmergencia(EmergenciaResponse emergencia) {
        // Prototipo: simulación de integración con sistema ya creado.
        // En una versión real aquí se llamaría a una API REST/SOAP, webhook o cola de mensajes.
        System.out.println("[SIMULACIÓN SISTEMA EXTERNO] Notificando emergencia "
                + emergencia.id()
                + " tipo " + emergencia.tipo()
                + " a " + externalUrl);
    }
}
