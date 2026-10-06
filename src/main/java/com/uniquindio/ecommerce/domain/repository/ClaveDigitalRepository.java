package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;

import java.util.Optional;
import java.util.UUID;

/**
  Repositorio de la entidad ClaveDigital.
  Los nombres describen el negocio no un mecanismo de persistencia.
 */
public interface ClaveDigitalRepository {

    Optional<ClaveDigital> obtenerPorId(UUID id);

    Optional<ClaveDigital> obtenerDisponiblePorOferta(UUID ofertaId);

    void guardar(ClaveDigital clave);
}
