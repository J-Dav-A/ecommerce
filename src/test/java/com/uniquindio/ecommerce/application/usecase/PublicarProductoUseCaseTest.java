package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicarProductoUseCaseTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final PublicarProductoUseCase publicarProducto = new PublicarProductoUseCase(ofertaRepository);
    private final Precio precio = new Precio(new BigDecimal("29.99"), "USD");

    @Test
    void deberiaPublicarYGuardarLaOferta() {
        // Act
        Oferta oferta = publicarProducto.ejecutar("o-1", "u-1", "Hades II", precio, 10, Plataforma.PC);

        // Assert
        Oferta guardada = ofertaRepository.obtenerPorId("o-1").orElseThrow();
        assertEquals(EstadoOferta.PUBLICADA, guardada.getEstado());
        assertEquals("Hades II", guardada.getTitulo());
        assertEquals(oferta.getId(), guardada.getId());
    }

    @Test
    void noDebePublicarUnaOfertaSinStock() {
        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> publicarProducto.ejecutar("o-2", "u-1", "Sin stock", precio, 0, Plataforma.PC));

        // Assert: la regla vive en el dominio y la oferta no se guardó
        assertEquals("No se puede publicar sin stock disponible", excepcion.getMessage());
        assertTrue(ofertaRepository.obtenerPorId("o-2").isEmpty());
    }

    @Test
    void noDebePublicarUnaOfertaSinPlataforma() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> publicarProducto.ejecutar("o-3", "u-1", "Sin plataforma", precio, 5, null));
        assertTrue(ofertaRepository.obtenerPorId("o-3").isEmpty());
    }
}