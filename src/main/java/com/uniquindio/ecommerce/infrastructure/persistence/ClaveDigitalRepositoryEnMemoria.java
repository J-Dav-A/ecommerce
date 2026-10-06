package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
  Implementación en memoria de ClaveDigitalRepository
  Guarda las claves en un HashMap mientras la aplicación esté corriendo.
 */
public class ClaveDigitalRepositoryEnMemoria implements ClaveDigitalRepository {

    private final Map<UUID, ClaveDigital> claves = new HashMap<>();

    @Override
    public Optional<ClaveDigital> obtenerPorId(UUID id) {
        return Optional.ofNullable(claves.get(id));
    }

    @Override
    public Optional<ClaveDigital> obtenerDisponiblePorOferta(UUID ofertaId) {
        return claves.values().stream()
                .filter(clave -> clave.getOfertaId().equals(ofertaId))
                .filter(clave -> clave.getEstado() == EstadoClaveDigital.DISPONIBLE)
                .findFirst();
    }

    @Override
    public void guardar(ClaveDigital clave) {
        claves.put(clave.getId(), clave);
    }
}