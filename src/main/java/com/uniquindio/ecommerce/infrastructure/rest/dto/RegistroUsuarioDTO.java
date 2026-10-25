package com.uniquindio.ecommerce.infrastructure.rest.dto;

public record RegistroUsuarioDTO(
        String nombre,
        String email,
        String password,
        String rol // ej: "COMPRADOR" o "VENDEDOR"
) {}