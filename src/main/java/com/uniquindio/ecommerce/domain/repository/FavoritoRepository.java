package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Favorito;

import java.util.List;

public interface FavoritoRepository {
    boolean existe(String compradorId, String ofertaId);
    void guardar(Favorito favorito);
    // Devuelve true si había un favorito que borrar
    boolean eliminar(String compradorId, String ofertaId);
    // El más reciente primero
    List<Favorito> obtenerPorCompradorId(String compradorId);
}