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
                                "DELITOS_GRAVES", "Emergencia", "ROJO", "Robos, asaltos o violencia", "shield-alert", "/ayuda/lsch/delitos-graves.gif",
                                List.of(
                                        new CatalogoResponse.Incidente("ROBO_CON_VIOLENCIA", "Robo con violencia", "siren", "FRASE_ROBO_VIOLENCIA"),
                                        new CatalogoResponse.Incidente("ASALTO_EN_CURSO", "Asalto en curso", "zap", "FRASE_ASALTO_CURSO"),
                                        new CatalogoResponse.Incidente("AGRESION_FISICA", "Agresión física", "hand", "FRASE_AGRESION_FISICA")
                                )
                        ),
                        new CatalogoResponse.CategoriaEmergencia(
                                "ASISTENCIA_VIAL", "Asistencia / Vial", "NARANJA", "Accidentes, sospechosos o desórdenes", "car-front", "/ayuda/lsch/asistencia-vial.gif",
                                List.of(
                                        new CatalogoResponse.Incidente("ACCIDENTE_TRANSITO", "Accidente de tránsito", "triangle-alert", "FRASE_ACCIDENTE_TRANSITO"),
                                        new CatalogoResponse.Incidente("PERSONA_SOSPECHOSA", "Persona sospechosa", "users", "FRASE_PERSONA_SOSPECHOSA"),
                                        new CatalogoResponse.Incidente("PELEAS_DESORDENES", "Peleas / Desórdenes", "flame", "FRASE_PELEAS_DESORDENES")
                                )
                        ),
                        new CatalogoResponse.CategoriaEmergencia(
                                "SOSPECHA_PREVENCION", "Sospecha y Prevención", "AZUL", "Algo extraño en el entorno", "eye", "/ayuda/lsch/sospecha-prevencion.gif",
                                List.of(
                                        new CatalogoResponse.Incidente("VEHICULO_SOSPECHOSO", "Vehículo sospechoso", "car", "FRASE_VEHICULO_SOSPECHOSO"),
                                        new CatalogoResponse.Incidente("PERSONA_MERODEANDO", "Persona merodeando", "footprints", "FRASE_PERSONA_MERODEANDO"),
                                        new CatalogoResponse.Incidente("MARCAJE_CASA", "Marcaje de casa", "home", "FRASE_MARCAJE_CASA")
                                )
                        )
                ),
                List.of(
                        new CatalogoResponse.FraseRapida("ALERTA_SILENCIOSA", "Alerta silenciosa", "siren"),
                        new CatalogoResponse.FraseRapida("FRASE_ROBO_VIOLENCIA", "Robo con violencia", "siren"),
                        new CatalogoResponse.FraseRapida("FRASE_ASALTO_CURSO", "Asalto en curso", "zap"),
                        new CatalogoResponse.FraseRapida("FRASE_AGRESION_FISICA", "Agresión física", "hand"),
                        new CatalogoResponse.FraseRapida("FRASE_ACCIDENTE_TRANSITO", "Accidente de tránsito", "triangle-alert"),
                        new CatalogoResponse.FraseRapida("FRASE_VEHICULO_SOSPECHOSO", "Vehículo sospechoso", "car")
                )
        );
    }
}
