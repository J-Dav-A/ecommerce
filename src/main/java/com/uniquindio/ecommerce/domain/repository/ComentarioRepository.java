package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ComentarioRepository {
    Optional<Comentario> obtenerPorId(String id);
    // Comentarios de la oferta, el más reciente primero
    List<Comentario> buscarPorOfertaId(String ofertaId);
    // Solo trae las ofertas que tienen al menos un comentario
    List<ResumenPorOferta> resumenPorOfertas(Collection<String> ofertaIds);
    boolean existePorCompraId(String compraId);
    void guardar(Comentario comentario);
}