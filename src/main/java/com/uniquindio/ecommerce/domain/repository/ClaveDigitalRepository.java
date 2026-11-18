package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import java.util.Optional;

public interface ClaveDigitalRepository {
    Optional<ClaveDigital> obtenerPorId(String id);
    Optional<ClaveDigital> obtenerDisponiblePorOferta(String ofertaId);
    // Clave asignada (o ya canjeada) que tiene un comprador para una oferta
    Optional<ClaveDigital> obtenerDeComprador(String ofertaId, String compradorId);
    void guardar(ClaveDigital clave);
}