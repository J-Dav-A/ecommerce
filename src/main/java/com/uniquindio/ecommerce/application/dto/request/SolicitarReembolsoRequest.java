package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Mapea a: Compra.solicitarReembolso()
public record SolicitarReembolsoRequest(

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
        String motivo
) {}