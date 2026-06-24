package com.mediaciondirecta.dto;

import com.mediaciondirecta.enums.RolMensaje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MensajeChatRequest(
        @NotNull RolMensaje rolEmisor,
        @NotBlank String mensaje
) {
}
