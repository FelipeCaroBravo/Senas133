package com.mediaciondirecta.dto;

import java.util.List;

public record CatalogoResponse(
        List<CategoriaEmergencia> categorias,
        List<FraseRapida> frasesRapidas
) {
    public record CategoriaEmergencia(
            String tipo,
            String titulo,
            String color,
            String descripcion,
            String icono,
            String ayudaLschUrl,
            List<Incidente> incidentes
    ) {
    }

    public record Incidente(
            String subtipo,
            String titulo,
            String icono,
            String fraseCodigo
    ) {
    }

    public record FraseRapida(
            String codigo,
            String texto,
            String icono
    ) {
    }
}
