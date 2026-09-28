package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Oferta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfertaRepository {

    Optional<Oferta> obtenerPorId(UUID id);

    List<Oferta> obtenerPublicadas();

    void guardar(Oferta oferta);
}