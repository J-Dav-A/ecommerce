package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CompraRepositoryEnMemoria implements CompraRepository {

    private final Map<String, Compra> compras = new ConcurrentHashMap<>();

    @Override
    public Optional<Compra> obtenerPorId(String id) {
        return Optional.ofNullable(compras.get(id));
    }

    @Override
    public void guardar(Compra compra) {
        compras.put(compra.getId(), compra);
    }

    @Override
    public boolean existeCompraCompletada(String compradorId, String ofertaId) {
        return compras.values().stream()
                .anyMatch(c -> c.getCompradorId().equals(compradorId)
                        && c.getOfertaId().equals(ofertaId)
                        && c.getEstado() == EstadoCompra.COMPLETADA);
    }

    @Override
    public List<Compra> obtenerPorCompradorId(String compradorId) {
        return compras.values().stream()
                .filter(c -> c.getCompradorId().equals(compradorId))
                .sorted(Comparator.comparing(Compra::getFechaCompra).reversed()) // igual que el repositorio JPA
                .toList();
    }

    @Override
    public List<Compra> obtenerPorOfertaId(String ofertaId) {
        return compras.values().stream()
                .filter(c -> c.getOfertaId().equals(ofertaId))
                .toList();
    }

    @Override
    public boolean existenComprasRecientes(String ofertaId, LocalDateTime desde) {
        return compras.values().stream()
                .anyMatch(c -> c.getOfertaId().equals(ofertaId)
                        && c.estaActiva()
                        && c.getFechaCompra().isAfter(desde));
    }
}