package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import java.util.List;
import java.util.Optional;

public interface ComentarioRepository {
    Optional<Comentario> obtenerPorId(String id);
    // Comentarios de la oferta, el más reciente primero
    List<Comentario> buscarPorOfertaId(String ofertaId);
    boolean existePorCompraId(String compraId);
    void guardar(Comentario comentario);
}