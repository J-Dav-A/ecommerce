package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CompraRepositoryEnMemoria implements CompraRepository {

    private final Map<String, Compra> compras = new HashMap<>();

    @Override
    public Optional<Compra> obtenerPorId(String id) {
        return Optional.ofNullable(compras.get(id));
    }

    @Override
    public boolean existeCompraActiva(String compradorId, String modeloId) {

        return compras.values().stream()
                .anyMatch(compra ->
                        compra.getCompradorId().equals(compradorId)
                                && compra.getModeloId().equals(modeloId)
                                && !compra.getEstado().esFinal()
                );
    }

    @Override
    public void guardar(Compra compra) {
        compras.put(compra.getId(), compra);
    }
}