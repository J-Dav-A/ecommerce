package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntregarClaveDigitalTest {

    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ClaveDigitalRepository claveRepository = new ClaveDigitalRepositoryEnMemoria();
    private final EntregarClaveDigital entregarClave = new EntregarClaveDigital(compraRepository, claveRepository);

    private void guardarCompra(String id, String compradorId, EstadoCompra estado) {
        compraRepository.guardar(Compra.reconstruir(id, "o-1", compradorId,
                new Precio(new BigDecimal("30"), "USD"), LocalDateTime.now(), estado));
    }

    private void guardarClaveAsignada(String id, String compradorId) {
        claveRepository.guardar(ClaveDigital.reconstruir(id, "o-1", "CODIGO-" + id, new LicenciaDigital(1, false),
                EstadoClaveDigital.ASIGNADA, compradorId, Instant.now()));
    }

    @Test
    void elDuenoDeUnaCompraCompletadaRecibeSuClaveYQuedaCanjeada() {
        // Arrange
        guardarCompra("c-1", "u-1", EstadoCompra.COMPLETADA);
        guardarClaveAsignada("k-1", "u-1");

        // Act
        ClaveDigital clave = entregarClave.ejecutar("c-1", "u-1");

        // Assert
        assertEquals("CODIGO-k-1", clave.getCodigo());
        assertEquals(EstadoClaveDigital.CANJEADA, claveRepository.obtenerPorId("k-1").orElseThrow().getEstado());
    }

    @Test
    void pedirLaClaveOtraVezDevuelveLaMismaSinErrores() {
        // Arrange
        guardarCompra("c-2", "u-1", EstadoCompra.COMPLETADA);
        guardarClaveAsignada("k-2", "u-1");
        entregarClave.ejecutar("c-2", "u-1");

        // Act
        ClaveDigital clave = entregarClave.ejecutar("c-2", "u-1");

        // Assert
        assertEquals("CODIGO-k-2", clave.getCodigo());
        assertEquals(EstadoClaveDigital.CANJEADA, clave.getEstado());
    }

    @Test
    void noDebeEntregarLaClaveDeUnaCompraPendiente() {
        // Arrange
        guardarCompra("c-3", "u-1", EstadoCompra.PENDIENTE);
        guardarClaveAsignada("k-3", "u-1");

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> entregarClave.ejecutar("c-3", "u-1"));

        // Assert: la clave sigue sin canjear
        assertEquals("La compra debe estar completada para recibir la clave", excepcion.getMessage());
        assertEquals(EstadoClaveDigital.ASIGNADA, claveRepository.obtenerPorId("k-3").orElseThrow().getEstado());
    }

    @Test
    void noDebeEntregarLaClaveDeLaCompraDeOtroComprador() {
        // Arrange
        guardarCompra("c-4", "u-1", EstadoCompra.COMPLETADA);
        guardarClaveAsignada("k-4", "u-1");

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> entregarClave.ejecutar("c-4", "u-9"));
        assertEquals(EstadoClaveDigital.ASIGNADA, claveRepository.obtenerPorId("k-4").orElseThrow().getEstado());
    }

    @Test
    void deberiaResponderNoEncontradaSiLaCompraNoTieneClave() {
        // Arrange
        guardarCompra("c-5", "u-1", EstadoCompra.COMPLETADA);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> entregarClave.ejecutar("c-5", "u-1"));
    }
}