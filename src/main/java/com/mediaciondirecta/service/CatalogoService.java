package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.CatalogoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoService {

    public CatalogoResponse obtenerCatalogo() {
        return new CatalogoResponse(
                List.of(
                        new CatalogoResponse.CategoriaEmergencia(
                                "DELITOS_GRAVES", "Delitos graves", "ROJO", "Robo, agresión o violencia", "shield-alert", "/ayuda/lsch/delitos-graves.gif",
                                List.of(
                                        new CatalogoResponse.Incidente("ROBO", "Robo", "hand-coins", "FRASE_ROBO"),
                                        new CatalogoResponse.Incidente("AGRESION", "Agresión", "alert-triangle", "FRASE_AGRESION"),
                                        new CatalogoResponse.Incidente("VIOLENCIA_INTRAFAMILIAR", "Violencia intrafamiliar", "home-alert", "FRASE_VIF")
                                )
                        ),
                        new CatalogoResponse.CategoriaEmergencia(
                                "ASISTENCIA_VIAL", "Asistencia / vial", "NARANJA", "Choque, accidente o ayuda en ruta", "car-front", "/ayuda/lsch/asistencia-vial.gif",
                                List.of(
                                        new CatalogoResponse.Incidente("ACCIDENTE_TRANSITO", "Accidente", "car-crash", "FRASE_ACCIDENTE"),
                                        new CatalogoResponse.Incidente("ASISTENCIA_VIAL", "Asistencia vial", "wrench", "FRASE_ASISTENCIA")
                                )
                        ),
                        new CatalogoResponse.CategoriaEmergencia(
                                "SOSPECHA_PREVENCION", "Sospecha / prevención", "AZUL", "Persona sospechosa o situación preventiva", "eye", "/ayuda/lsch/sospecha-prevencion.gif",
                                List.of(
                                        new CatalogoResponse.Incidente("PERSONA_SOSPECHOSA", "Persona sospechosa", "user-search", "FRASE_SOSPECHA"),
                                        new CatalogoResponse.Incidente("RUIDOS_O_DISTURBIOS", "Disturbios", "volume-2", "FRASE_DISTURBIOS"),
                                        new CatalogoResponse.Incidente("OTRO", "Otro", "circle-help", "FRASE_OTRO")
                                )
                        )
                ),
                List.of(
                        new CatalogoResponse.FraseRapida("FRASE_ROBO", "Me están robando", "hand-coins"),
                        new CatalogoResponse.FraseRapida("FRASE_AGRESION", "Necesito ayuda urgente", "alert-triangle"),
                        new CatalogoResponse.FraseRapida("FRASE_VIF", "Estoy en peligro en mi casa", "home-alert"),
                        new CatalogoResponse.FraseRapida("FRASE_ACCIDENTE", "Hubo un accidente", "car-crash"),
                        new CatalogoResponse.FraseRapida("FRASE_SOSPECHA", "Veo una situación sospechosa", "eye")
                )
        );
    }
}
