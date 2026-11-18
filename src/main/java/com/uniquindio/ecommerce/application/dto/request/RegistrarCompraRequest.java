package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// Mapea a: RegistrarCompra.ejecutar()
// El comprador no viene en el cuerpo: se toma del token JWT del usuario autenticado.
public record RegistrarCompraRequest(

        @NotBlank(message = "La oferta es obligatoria")
        String ofertaId
) {}