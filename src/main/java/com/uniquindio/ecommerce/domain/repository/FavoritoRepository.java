
package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Favorito;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository {

    Optional<Favorito> obtenerPorCompradorYOferta(String compradorId, String ofertaId);

    List<String> buscarOfertaIdsPorCompradorId(String compradorId);

    void guardar(Favorito favorito);

    void eliminar(String compradorId, String ofertaId);
}
