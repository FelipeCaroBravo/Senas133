package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank String nombreCompleto,
        @NotBlank String rut,
        @NotBlank String numeroDocumento,
        @NotBlank String telefono,
        @NotBlank @Size(min = 4, max = 8) @Pattern(regexp = "^[0-9]+$", message = "El PIN debe contener solo números") String pin
) {
}
