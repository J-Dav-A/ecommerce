package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Compra;

import java.util.Optional;

public interface CompraRepository {

    Optional<Compra> obtenerPorId(String id);

    boolean existeCompraActiva(String compradorId, String modeloId);

    void guardar(Compra compra);
}