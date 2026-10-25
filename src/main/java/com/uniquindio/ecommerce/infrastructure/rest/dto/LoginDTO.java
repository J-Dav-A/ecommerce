package com.uniquindio.ecommerce.infrastructure.rest.dto;

public record LoginDTO(
        String email,
        String password
) {}