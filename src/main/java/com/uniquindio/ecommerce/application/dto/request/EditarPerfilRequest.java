package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// El email y el rol no se pueden cambiar
public record EditarPerfilRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        LocalDate fechaNacimiento,

        @Size(max = 500, message = "La foto de perfil no puede superar 500 caracteres")
        String fotoPerfil,

        // Solo vendedores
        @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
        String descripcion,

        // Solo vendedores
        @Size(max = 500, message = "Los datos de pago no pueden superar 500 caracteres")
        String datosPago
) {}