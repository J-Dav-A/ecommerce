package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// Mapea a: RegistrarCompra.ejecutar()
public record RegistrarCompraRequest(

        @NotBlank(message = "La oferta es obligatoria")
        String ofertaId,

        @NotBlank(message = "El comprador es obligatorio")
        String compradorId
) {}