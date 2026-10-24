package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DejarComentarioRequest(
        @NotBlank(message = "El comprador es obligatorio")
        String compradorId,

        @NotBlank(message = "La oferta es obligatoria")
        String ofertaId,

        @NotBlank(message = "El comentario no puede estar vacío")
        String texto
) {}