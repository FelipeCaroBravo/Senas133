package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginPinRequest(
        Long usuarioId,
        String rut,
        @NotBlank String pin
) {
}
