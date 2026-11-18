package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultarHistorialDeComprasTest {

    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ConsultarHistorialDeCompras consultarHistorial = new ConsultarHistorialDeCompras(compraRepository);

    private final Precio precio = new Precio(new BigDecimal("15"), "USD");

    @Test
    void deberiaDevolverSoloLasComprasDelComprador() {
        // Arrange
        compraRepository.guardar(Compra.realizar("c-1", "o-1", "u-1", precio));
        compraRepository.guardar(Compra.realizar("c-2", "o-2", "u-1", precio));
        compraRepository.guardar(Compra.realizar("c-3", "o-1", "u-2", precio));

        // Act
        List<Compra> historial = consultarHistorial.ejecutar("u-1");

        // Assert
        assertEquals(2, historial.size());
        assertTrue(historial.stream().allMatch(c -> c.getCompradorId().equals("u-1")));
    }

    @Test
    void deberiaDevolverUnaListaVaciaSiElCompradorNoHaComprado() {
        // Arrange: no hay compras guardadas

        // Act
        List<Compra> historial = consultarHistorial.ejecutar("u-1");

        // Assert
        assertTrue(historial.isEmpty());
    }
}