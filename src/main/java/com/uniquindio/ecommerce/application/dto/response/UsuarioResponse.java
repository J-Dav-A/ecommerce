package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Usuario;

import java.time.LocalDate;

public record UsuarioResponse(String id, String nombre, String email, String rol, String telefono, LocalDate fechaNacimiento,
                              String fotoPerfil, String descripcion, String datosPago) {
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol(),
                usuario.getTelefono(), usuario.getFechaNacimiento(),
                usuario.getFotoPerfil(), usuario.getDescripcion(), usuario.getDatosPago());
    }
}