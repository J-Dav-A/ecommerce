package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;

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
                .sorted(Comparator.comparing(Comentario::getFecha).reversed())
                .toList();
    }

    @Override
    public List<ResumenPorOferta> resumenPorOfertas(Collection<String> ofertaIds) {
        return ofertaIds.stream().distinct()
                .map(id -> {
                    List<Comentario> deOferta = comentarios.values().stream()
                            .filter(c -> c.getOfertaId().equals(id)).toList();
                    if (deOferta.isEmpty()) return null;
                    double promedio = deOferta.stream().mapToInt(Comentario::getCalificacion).average().orElse(0);
                    return new ResumenPorOferta(id, promedio, (long) deOferta.size());
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean existePorCompraId(String compraId) {
        return comentarios.values().stream().anyMatch(c -> c.getCompraId().equals(compraId));
    }

    @Override
    public void guardar(Comentario comentario) {
        comentarios.put(comentario.getId(), comentario);
    }
}