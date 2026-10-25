package com.uniquindio.ecommerce.infrastructure.rest.dto;

public record TokenDTO(
        String token,
        String tipo,
        String email
) {
    public TokenDTO(String token, String email) {
        this(token, "Bearer", email);
    }
}