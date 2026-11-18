package com.uniquindio.ecommerce.infrastructure.rest.dto;

// Respuesta del login. El rol y los datos básicos permiten al frontend armar el menú y proteger
// rutas por rol (la seguridad real sigue estando en el backend: el rol que cuenta es el del token).
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