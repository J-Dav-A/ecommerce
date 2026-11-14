package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarOfertaTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final EliminarOferta eliminarOferta = new EliminarOferta(ofertaRepository, compraRepository);
    private final Precio precio = new Precio(new BigDecimal("19.99"), "COP");

    private Oferta guardarOferta(String id, String vendedorId) {
        Oferta oferta = Oferta.crear(id, vendedorId, "Juego a eliminar", precio, 3);
        ofertaRepository.guardar(oferta);
        return oferta;
    }

    @Test
    void alEjecutarSeCambiaElEstadoAEliminadaSinBorrarla() {
        // Arrange
        guardarOferta("o-1", "v-1");

        // Act
        eliminarOferta.ejecutar("o-1", "v-1");

        // Assert
        Oferta ofertaEliminada = ofertaRepository.obtenerPorId("o-1").orElseThrow();
        assertEquals(EstadoOferta.ELIMINADA, ofertaEliminada.getEstado());
    }

    @Test
    void noDebeEliminarUnaOfertaQueNoExiste() {
        // Act
        RecursoNoEncontradoException excepcion = assertThrows(
                RecursoNoEncontradoException.class,
                () -> eliminarOferta.ejecutar("no-existe", "v-1"));

        // Assert
        assertEquals("La oferta no existe", excepcion.getMessage());
    }

    @Test
    void noDebeEliminarLaOfertaDeOtroVendedor() {
        // Arrange
        guardarOferta("o-2", "v-1");

        // Act & Assert: v-9 no es el dueño, la oferta se trata como inexistente y no cambia
        assertThrows(RecursoNoEncontradoException.class, () -> eliminarOferta.ejecutar("o-2", "v-9"));
        assertEquals(EstadoOferta.BORRADOR, ofertaRepository.obtenerPorId("o-2").orElseThrow().getEstado());
    }

    @Test
    void noDebeEliminarUnaOfertaConComprasRecientesSujetasAReembolso() {
        // Arrange: una compra de hace 1 hora, todavía dentro del plazo de reembolso (48 h)
        guardarOferta("o-3", "v-1");
        compraRepository.guardar(Compra.reconstruir("c-1", "o-3", "u-1", precio,
                LocalDateTime.now().minusHours(1), EstadoCompra.COMPLETADA));

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> eliminarOferta.ejecutar("o-3", "v-1"));

        // Assert
        assertEquals("No se puede eliminar la oferta: tiene compras recientes sujetas a reembolso",
                excepcion.getMessage());
        assertEquals(EstadoOferta.BORRADOR, ofertaRepository.obtenerPorId("o-3").orElseThrow().getEstado());
    }

    @Test
    void deberiaEliminarUnaOfertaCuyasComprasYaPasaronElPlazoDeReembolso() {
        // Arrange: compra de hace 5 días (fuera del plazo) y otra reembolsada reciente
        guardarOferta("o-4", "v-1");
        compraRepository.guardar(Compra.reconstruir("c-2", "o-4", "u-1", precio,
                LocalDateTime.now().minusDays(5), EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-3", "o-4", "u-2", precio,
                LocalDateTime.now().minusHours(2), EstadoCompra.REEMBOLSADA));

        // Act
        eliminarOferta.ejecutar("o-4", "v-1");

        // Assert
        assertEquals(EstadoOferta.ELIMINADA, ofertaRepository.obtenerPorId("o-4").orElseThrow().getEstado());
    }
}