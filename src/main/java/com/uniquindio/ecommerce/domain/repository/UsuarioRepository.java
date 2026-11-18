package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> obtenerPorId(String id);
    Optional<Usuario> obtenerPorEmail(String email);
    boolean existePorEmail(String email);
    void guardar(Usuario usuario);
}