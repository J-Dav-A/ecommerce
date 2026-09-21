package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OfertaRepositoryEnMemoria implements OfertaRepository {

    private final Map<UUID, Oferta> ofertas = new HashMap<>();

    @Override
    public Optional<Oferta> obtenerPorId(UUID id) {
        return Optional.ofNullable(ofertas.get(id));
    }

    @Override
    public void guardar(Oferta oferta) {
        ofertas.put(oferta.getId(), oferta);
    }
}