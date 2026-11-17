
package com.uniquindio.ecommerce.infrastructure.rest.dto;

public record TokenDTO(
        String token,
        String tipo,
        String email,
        String rol,
        String usuarioId,
        String nombre
) {
    public TokenDTO(String token, String email, String rol, String usuarioId, String nombre) {
        this(token, "Bearer", email, rol, usuarioId, nombre);
    }
}
