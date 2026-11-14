package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// El comprador no viene en el cuerpo: se toma del token JWT del usuario autenticado.
public record DejarComentarioRequest(
        @NotBlank(message = "La oferta es obligatoria")
        String ofertaId,

        @NotBlank(message = "El comentario no puede estar vacío")
        String texto
) {}