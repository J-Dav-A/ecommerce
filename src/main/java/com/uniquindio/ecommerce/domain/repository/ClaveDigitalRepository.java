package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import java.util.Optional;

public interface ClaveDigitalRepository {
    Optional<ClaveDigital> obtenerPorId(String id);
    Optional<ClaveDigital> obtenerDisponiblePorOferta(String ofertaId);
    void guardar(ClaveDigital clave);
}