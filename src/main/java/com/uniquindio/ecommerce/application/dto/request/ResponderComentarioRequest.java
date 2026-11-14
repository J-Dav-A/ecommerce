package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResponderComentarioRequest(
        @NotBlank(message = "La respuesta no puede estar vacía")
        @Size(max = 500, message = "La respuesta no puede superar 500 caracteres")
        String respuesta
) {}