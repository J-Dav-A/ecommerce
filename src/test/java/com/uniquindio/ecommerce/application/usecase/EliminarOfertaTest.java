package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminarOfertaTest {

    @Test
    void alEjecutarSeCambiaElEstadoAEliminadaSinBorrarla() {
        // Arrange
        OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();

        Oferta oferta = Oferta.crear(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "Juego a eliminar",
                new Precio(new BigDecimal("19.99"), "COP"), 3);
        ofertaRepository.guardar(oferta);

        EliminarOferta eliminarOferta = new EliminarOferta(ofertaRepository);

        // Act
        eliminarOferta.ejecutar(oferta.getId());

        // Assert
        Oferta ofertaEliminada = ofertaRepository.obtenerPorId(oferta.getId()).orElseThrow();
        assertEquals(EstadoOferta.ELIMINADA, ofertaEliminada.getEstado());
    }

    @Test
    void noDebeEliminarUnaOfertaQueNoExiste() {
        // Arrange
        EliminarOferta eliminarOferta = new EliminarOferta(new OfertaRepositoryEnMemoria());

        // Act
        RecursoNoEncontradoException excepcion = assertThrows(
                RecursoNoEncontradoException.class,
                () -> eliminarOferta.ejecutar("no-existe"));

        // Assert
        assertEquals("La oferta no existe", excepcion.getMessage());
    }
}