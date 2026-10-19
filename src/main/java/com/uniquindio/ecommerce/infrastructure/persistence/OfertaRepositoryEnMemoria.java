package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OfertaRepositoryEnMemoria implements OfertaRepository {

    private final Map<String, Oferta> ofertas = new ConcurrentHashMap<>();

    @Override
    public Optional<Oferta> obtenerPorId(String id) {
        return Optional.ofNullable(ofertas.get(id));
    }

    @Override
    public List<Oferta> obtenerPublicadas() {
        return ofertas.values().stream()
                .filter(o -> o.getEstado() == EstadoOferta.PUBLICADA)
                .toList();
    }

    @Override
    public void guardar(Oferta oferta) {
        ofertas.put(oferta.getId(), oferta);
    }
}