package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsuarioRepositoryEnMemoria implements UsuarioRepository {

    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    @Override
    public Optional<Usuario> obtenerPorId(String id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarios.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }
}