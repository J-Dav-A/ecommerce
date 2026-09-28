package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UsuarioRepositoryEnMemoria implements UsuarioRepository {

    private final Map<UUID, Usuario> usuarios = new HashMap<>();

    @Override
    public Optional<Usuario> obtenerPorId(UUID id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarios.values()
                .stream()
                .anyMatch(usuario ->
                        usuario.getEmail().equalsIgnoreCase(email)
                );
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }
}