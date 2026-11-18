package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarHistorialDeCompras {

    private final CompraRepository repository;

    @Transactional(readOnly = true)
    public List<Compra> ejecutar(String compradorId) {
        return ejecutar(compradorId, null, null, null);
    }

    // Filtros opcionales: rango de fechas (inclusivo) y estado; el repositorio ya trae el más reciente primero
    @Transactional(readOnly = true)
    public List<Compra> ejecutar(String compradorId, LocalDate desde, LocalDate hasta, EstadoCompra estado) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaDominioException("La fecha inicial no puede ser posterior a la final");
        }
        return repository.obtenerPorCompradorId(compradorId).stream()
                .filter(c -> estado == null || c.getEstado() == estado)
                .filter(c -> desde == null || !c.getFechaCompra().isBefore(desde.atStartOfDay()))
                .filter(c -> hasta == null || c.getFechaCompra().isBefore(hasta.plusDays(1).atStartOfDay()))
                .toList();
    }
}