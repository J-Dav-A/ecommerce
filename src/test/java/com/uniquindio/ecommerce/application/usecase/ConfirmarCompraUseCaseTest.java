package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfirmarCompraUseCaseTest {

    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ConfirmarCompraUseCase confirmarCompra = new ConfirmarCompraUseCase(compraRepository);

    @Test
    void deberiaPasarUnaCompraPendienteACompletada() {
        // Arrange
        Compra compra = Compra.realizar("c-1", "o-1", "u-1", new Precio(new BigDecimal("10"), "USD"));
        compraRepository.guardar(compra);

        // Act
        confirmarCompra.ejecutar("c-1", "u-1");

        // Assert
        assertEquals(EstadoCompra.COMPLETADA, compraRepository.obtenerPorId("c-1").orElseThrow().getEstado());
    }

    @Test
    void noDebeConfirmarUnaCompraQueNoExiste() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> confirmarCompra.ejecutar("no-existe", "u-1"));
    }

    @Test
    void noDebeConfirmarUnaCompraYaReembolsada() {
        // Arrange
        Compra reembolsada = Compra.reconstruir("c-2", "o-1", "u-1", new Precio(new BigDecimal("10"), "USD"),
                LocalDateTime.now(), EstadoCompra.REEMBOLSADA);
        compraRepository.guardar(reembolsada);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> confirmarCompra.ejecutar("c-2", "u-1"));
        assertEquals(EstadoCompra.REEMBOLSADA, compraRepository.obtenerPorId("c-2").orElseThrow().getEstado());
    }

    @Test
    void noDebeConfirmarLaCompraDeOtroComprador() {
        // Arrange
        Compra compra = Compra.realizar("c-3", "o-1", "u-1", new Precio(new BigDecimal("10"), "USD"));
        compraRepository.guardar(compra);

        // Act & Assert: u-9 no es el dueño, la compra se trata como inexistente y sigue PENDIENTE
        assertThrows(RecursoNoEncontradoException.class, () -> confirmarCompra.ejecutar("c-3", "u-9"));
        assertEquals(EstadoCompra.PENDIENTE, compraRepository.obtenerPorId("c-3").orElseThrow().getEstado());
    }
}