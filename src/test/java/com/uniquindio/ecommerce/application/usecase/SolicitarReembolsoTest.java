package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitarReembolsoTest {

    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ClaveDigitalRepository claveRepository = new ClaveDigitalRepositoryEnMemoria();
    private final SolicitarReembolso solicitarReembolso = new SolicitarReembolso(compraRepository, claveRepository);

    private Compra guardarCompra(String id, String compradorId, EstadoCompra estado) {
        Compra compra = Compra.reconstruir(id, "o-1", compradorId, new Precio(new BigDecimal("20"), "USD"),
                LocalDateTime.now(), estado);
        compraRepository.guardar(compra);
        return compra;
    }

    @Test
    void elDuenoDeUnaCompraCompletadaPuedeReembolsarla() {
        // Arrange
        guardarCompra("c-1", "u-1", EstadoCompra.COMPLETADA);

        // Act
        solicitarReembolso.ejecutar("c-1", "u-1", "No era el juego que esperaba");

        // Assert
        assertEquals(EstadoCompra.REEMBOLSADA, compraRepository.obtenerPorId("c-1").orElseThrow().getEstado());
    }

    @Test
    void noDebePermitirReembolsarLaCompraDeOtroComprador() {
        // Arrange
        guardarCompra("c-2", "u-1", EstadoCompra.COMPLETADA);

        // Act & Assert: u-9 no es el dueño, la compra se trata como inexistente y no cambia
        assertThrows(RecursoNoEncontradoException.class,
                () -> solicitarReembolso.ejecutar("c-2", "u-9", "Quiero mi dinero"));
        assertEquals(EstadoCompra.COMPLETADA, compraRepository.obtenerPorId("c-2").orElseThrow().getEstado());
    }

    @Test
    void noDebeReembolsarUnaCompraQueTodaviaEstaPendiente() {
        // Arrange
        guardarCompra("c-3", "u-1", EstadoCompra.PENDIENTE);

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> solicitarReembolso.ejecutar("c-3", "u-1", "Motivo"));

        // Assert
        assertEquals("Solo se puede reembolsar una compra completada", excepcion.getMessage());
    }

    @Test
    void noDebeReembolsarSinMotivo() {
        // Arrange
        guardarCompra("c-4", "u-1", EstadoCompra.COMPLETADA);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> solicitarReembolso.ejecutar("c-4", "u-1", " "));
        assertEquals(EstadoCompra.COMPLETADA, compraRepository.obtenerPorId("c-4").orElseThrow().getEstado());
    }

    private ClaveDigital guardarClave(String id, EstadoClaveDigital estado, Instant fechaAsignacion) {
        ClaveDigital clave = ClaveDigital.reconstruir(id, "o-1", "CODIGO-" + id, new LicenciaDigital(1, false),
                estado, "u-1", fechaAsignacion);
        claveRepository.guardar(clave);
        return clave;
    }

    @Test
    void alReembolsarSeInvalidaLaClaveQueTenia() {
        // Arrange
        guardarCompra("c-5", "u-1", EstadoCompra.COMPLETADA);
        guardarClave("k-1", EstadoClaveDigital.ASIGNADA, Instant.now().minus(1, ChronoUnit.HOURS));

        // Act
        solicitarReembolso.ejecutar("c-5", "u-1", "No me gustó");

        // Assert
        assertEquals(EstadoClaveDigital.REEMBOLSADA, claveRepository.obtenerPorId("k-1").orElseThrow().getEstado());
        assertEquals(EstadoCompra.REEMBOLSADA, compraRepository.obtenerPorId("c-5").orElseThrow().getEstado());
    }

    @Test
    void noDebeReembolsarSiLaClaveYaFueCanjeada() {
        // Arrange
        guardarCompra("c-6", "u-1", EstadoCompra.COMPLETADA);
        guardarClave("k-2", EstadoClaveDigital.CANJEADA, Instant.now().minus(1, ChronoUnit.HOURS));

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> solicitarReembolso.ejecutar("c-6", "u-1", "Quiero mi dinero"));

        // Assert
        assertEquals("No se reembolsan claves ya canjeadas", excepcion.getMessage());
        assertEquals(EstadoClaveDigital.CANJEADA, claveRepository.obtenerPorId("k-2").orElseThrow().getEstado());
    }

    @Test
    void noDebeReembolsarFueraDelPlazoDe48Horas() {
        // Arrange: la clave se asignó hace 3 días
        guardarCompra("c-7", "u-1", EstadoCompra.COMPLETADA);
        guardarClave("k-3", EstadoClaveDigital.ASIGNADA, Instant.now().minus(3, ChronoUnit.DAYS));

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> solicitarReembolso.ejecutar("c-7", "u-1", "Quiero mi dinero"));

        // Assert
        assertEquals("El plazo de reembolso expiró", excepcion.getMessage());
        assertEquals(EstadoClaveDigital.ASIGNADA, claveRepository.obtenerPorId("k-3").orElseThrow().getEstado());
    }
}