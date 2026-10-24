package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import java.util.List;
import java.util.Optional;

public interface ComentarioRepository {
    Optional<Comentario> obtenerPorId(String id);
    List<Comentario> buscarPorOfertaId(String ofertaId);
    void guardar(Comentario comentario);
}