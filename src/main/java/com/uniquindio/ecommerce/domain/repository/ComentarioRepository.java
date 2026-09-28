package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Comentario;

import java.util.Optional;
import java.util.UUID;

public interface ComentarioRepository {

    Optional<Comentario> obtenerPorId(UUID id);

    boolean existePorCompraId(String compraId);

    void guardar(Comentario comentario);
}