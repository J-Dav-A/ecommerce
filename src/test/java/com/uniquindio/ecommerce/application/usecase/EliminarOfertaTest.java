package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EliminarOfertaTest {

    @Test
    void alEjecutarSeCambiaElEstadoAEliminadaSinBorrarla() {
        // Arrange
        OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();

        Oferta oferta = Oferta.crear(UUID.randomUUID(), UUID.randomUUID(), "Juego a eliminar",
                new Precio(new BigDecimal("19.99")), 3);
        ofertaRepository.guardar(oferta);

        EliminarOferta eliminarOferta = new EliminarOferta(ofertaRepository);

        // Act
        eliminarOferta.ejecutar(oferta.getId());

        // Assert
        Oferta ofertaEliminada = ofertaRepository.obtenerPorId(oferta.getId()).orElseThrow();
        assertEquals(EstadoOferta.ELIMINADA, ofertaEliminada.getEstado());
    }
}
