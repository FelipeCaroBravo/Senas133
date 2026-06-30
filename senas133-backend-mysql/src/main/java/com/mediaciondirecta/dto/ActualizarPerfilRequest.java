package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;

public record ActualizarPerfilRequest(
        @NotBlank String direccionPrincipal,
        @NotBlank String telefono
) {
}
