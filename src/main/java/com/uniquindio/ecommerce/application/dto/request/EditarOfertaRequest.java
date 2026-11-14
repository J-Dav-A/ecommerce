package com.uniquindio.ecommerce.application.dto.request;

import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

// imagenes es opcional: si no se envía, la galería actual no cambia; si se envía, la reemplaza (máx. 10 URLs)
public record EditarOfertaRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 120, message = "El título no puede superar 120 caracteres")
        String titulo,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        BigDecimal precioValor,

        @NotBlank(message = "La moneda es obligatoria")
        String precioMoneda,

        @Min(value = 0, message = "El stock no puede ser negativo")
        int stock,

        @NotNull(message = "La plataforma es obligatoria")
        Plataforma plataforma,

        List<String> imagenes
) {}