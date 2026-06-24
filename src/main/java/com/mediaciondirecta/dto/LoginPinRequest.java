package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginPinRequest(
        @NotBlank String rut,
        @NotBlank String pin
) {
}
