package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FavoritoRepositoryEnMemoria implements FavoritoRepository {

    private final Map<String, Favorito> favoritos = new ConcurrentHashMap<>();

    @Override
    public boolean existe(String compradorId, String ofertaId) {
        return favoritos.values().stream()
                .anyMatch(f -> f.getCompradorId().equals(compradorId) && f.getOfertaId().equals(ofertaId));
    }

    @Override
    public void guardar(Favorito favorito) {
        favoritos.put(favorito.getId(), favorito);
    }

    @Override
    public boolean eliminar(String compradorId, String ofertaId) {
        return favoritos.values().removeIf(
                f -> f.getCompradorId().equals(compradorId) && f.getOfertaId().equals(ofertaId));
    }

    @Override
    public List<Favorito> obtenerPorCompradorId(String compradorId) {
        return favoritos.values().stream()
                .filter(f -> f.getCompradorId().equals(compradorId))
                .sorted(Comparator.comparing(Favorito::getFecha).reversed())
                .toList();
    }
}