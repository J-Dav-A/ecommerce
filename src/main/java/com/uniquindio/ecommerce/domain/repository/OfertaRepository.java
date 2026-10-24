package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;

import java.util.List;
import java.util.Optional;

public interface OfertaRepository {
    Optional<Oferta> obtenerPorId(String id);
    List<Oferta> obtenerPublicadas();
    Pagina<Oferta> buscar(String texto, Plataforma plataforma, int pagina, int tamano);
    List<OfertaVentas> masVendidas(int limite);
    void guardar(Oferta oferta);
}