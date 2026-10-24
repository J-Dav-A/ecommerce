package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClaveDigitalRepositoryEnMemoria implements ClaveDigitalRepository {

    private final Map<String, ClaveDigital> claves = new ConcurrentHashMap<>();

    @Override
    public Optional<ClaveDigital> obtenerPorId(String id) {
        return Optional.ofNullable(claves.get(id));
    }

    @Override
    public Optional<ClaveDigital> obtenerDisponiblePorOferta(String ofertaId) {
        return claves.values().stream()
                .filter(c -> c.getOfertaId().equals(ofertaId) && c.getEstado() == EstadoClaveDigital.DISPONIBLE)
                .findFirst();
    }

    @Override
    public void guardar(ClaveDigital clave) {
        claves.put(clave.getId(), clave);
    }
}