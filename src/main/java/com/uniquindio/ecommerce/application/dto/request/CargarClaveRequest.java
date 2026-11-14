package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CargarClaveRequest(
        @NotBlank(message = "El código de la clave es obligatorio")
        @Size(max = 60, message = "El código no puede superar 60 caracteres")
        String codigo,

        @Min(value = 1, message = "La licencia debe permitir al menos una activación")
        int maxActivaciones,

        boolean transferible
) {}