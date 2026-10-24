package com.uniquindio.ecommerce.application.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegistrarUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String contrasena,

        String telefono,

        @NotBlank(message = "El rol es obligatorio")
        String rol,

        LocalDate fechaNacimiento
) {}