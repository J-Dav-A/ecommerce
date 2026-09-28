package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ComentarioRepositoryEnMemoria implements ComentarioRepository {

    private final Map<UUID, Comentario> comentarios = new HashMap<>();

    @Override
    public Optional<Comentario> obtenerPorId(UUID id) {
        return Optional.ofNullable(comentarios.get(id));
    }

    @Override
    public boolean existePorCompraId(String compraId) {
        return comentarios.values()
                .stream()
                .anyMatch(comentario ->
                        comentario.getCompraId().equals(compraId)
                );
    }

    @Override
    public void guardar(Comentario comentario) {
        comentarios.put(comentario.getId(), comentario);
    }
}