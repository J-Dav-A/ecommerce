package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Compra;
import java.util.List;
import java.util.Optional;

public interface CompraRepository {
    Optional<Compra> obtenerPorId(String id);
    void guardar(Compra compra);
    boolean existeCompraCompletada(String compradorId, String ofertaId);
    List<Compra> obtenerPorCompradorId(String compradorId);
}