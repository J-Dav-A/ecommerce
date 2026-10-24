package com.uniquindio.ecommerce.application.dto.request;

import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PublicarOfertaRequest(
        @NotBlank(message = "El vendedor es obligatorio")
        String vendedorId,

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        BigDecimal precioValor,

        @NotBlank(message = "La moneda es obligatoria")
        String precioMoneda,

        @Min(value = 0, message = "El stock no puede ser negativo")
        int stock,

        @NotNull(message = "La plataforma es obligatoria")
        Plataforma plataforma
) {}