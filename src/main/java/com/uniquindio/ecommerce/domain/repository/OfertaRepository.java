package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import java.util.List;
import java.util.Optional;

public interface OfertaRepository {
    Optional<Oferta> obtenerPorId(String id);
    List<Oferta> obtenerPublicadas();
    void guardar(Oferta oferta);
}