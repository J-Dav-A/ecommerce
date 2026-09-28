package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Compra;
import java.util.List;
import java.util.Optional;

public interface CompraRepository {

    Optional<Compra> obtenerPorId(String id);

    boolean existeCompraActiva(String compradorId, String modeloId);

    List<Compra> obtenerPorCompradorId(String compradorId);

    void guardar(Compra compra);


}