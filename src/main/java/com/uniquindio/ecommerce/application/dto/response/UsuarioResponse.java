package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Usuario;

public record UsuarioResponse(String id, String nombre, String email, String rol) {
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}