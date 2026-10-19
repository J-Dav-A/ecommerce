package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ComentarioRepositoryEnMemoria implements ComentarioRepository {

    private final Map<String, Comentario> comentarios = new ConcurrentHashMap<>();

    @Override
    public Optional<Comentario> obtenerPorId(String id) {
        return Optional.ofNullable(comentarios.get(id));
    }

    @Override
    public List<Comentario> buscarPorOfertaId(String ofertaId) {
        return comentarios.values().stream()
                .filter(c -> c.getOfertaId().equals(ofertaId))
                .toList();
    }

    @Override
    public void guardar(Comentario comentario) {
        comentarios.put(comentario.getId(), comentario);
    }
}