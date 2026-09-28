package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    Optional<Usuario> obtenerPorId(UUID id);

    boolean existePorEmail(String email);

    void guardar(Usuario usuario);
}