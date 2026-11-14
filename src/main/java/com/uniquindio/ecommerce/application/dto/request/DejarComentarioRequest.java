package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// El comprador no viene en el cuerpo: se toma del token JWT del usuario autenticado.
public record DejarComentarioRequest(
        @NotBlank(message = "La oferta es obligatoria")
        String ofertaId,

        @NotBlank(message = "El comentario no puede estar vacío")
        @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
        String texto,

        @Min(value = 1, message = "La calificación debe estar entre 1 y 5")
        @Max(value = 5, message = "La calificación debe estar entre 1 y 5")
        int calificacion
) {}