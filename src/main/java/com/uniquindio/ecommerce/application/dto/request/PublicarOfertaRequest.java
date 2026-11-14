package com.uniquindio.ecommerce.application.dto.request;

import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// El vendedor no viene en el cuerpo: se toma del token JWT del usuario autenticado.
public record PublicarOfertaRequest(
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