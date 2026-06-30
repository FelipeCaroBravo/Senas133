package com.mediaciondirecta.dto;

import jakarta.validation.constraints.NotNull;

public record MarcarLugarSeguroRequest(
        @NotNull Boolean lugarSeguroParaMasInformacion
) {
}
