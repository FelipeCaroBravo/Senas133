package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotBlank;

public record AsignarInterpreteRequest(
        @NotBlank String nombreInterprete,
        @NotBlank String urlSalaVideo
) {
}
