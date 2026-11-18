package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EditarOfertaTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final EditarOferta editarOferta = new EditarOferta(ofertaRepository);
    private final Precio precioNuevo = new Precio(new BigDecimal("45"), "USD");

    @BeforeEach
    void preparar() {
        Oferta oferta = Oferta.crear("o-1", "v-1", "Hades", new Precio(new BigDecimal("30"), "USD"), 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        oferta.definirImagenes(List.of("https://img.test/vieja.png"));
        ofertaRepository.guardar(oferta);
    }

    @Test
    void elDuenoEditaLosDatosYReemplazaLasImagenes() {
        // Act
        editarOferta.ejecutar("o-1", "v-1", "Hades II", precioNuevo, 9, Plataforma.NINTENDO,
                List.of("https://img.test/nueva.png"));

        // Assert
        Oferta guardada = ofertaRepository.obtenerPorId("o-1").orElseThrow();
        assertEquals("Hades II", guardada.getTitulo());
        assertEquals(9, guardada.getStock());
        assertEquals(Plataforma.NINTENDO, guardada.getPlataforma());
        assertEquals(List.of("https://img.test/nueva.png"), guardada.getImagenes());
    }

    @Test
    void sinListaDeImagenesLaGaleriaActualNoCambia() {
        // Act
        editarOferta.ejecutar("o-1", "v-1", "Hades II", precioNuevo, 9, Plataforma.PC, null);

        // Assert
        assertEquals(List.of("https://img.test/vieja.png"), ofertaRepository.obtenerPorId("o-1").orElseThrow().getImagenes());
    }

    @Test
    void unaListaVaciaBorraLaGaleria() {
        // Act
        editarOferta.ejecutar("o-1", "v-1", "Hades", precioNuevo, 5, Plataforma.PC, List.of());

        // Assert
        assertEquals(List.of(), ofertaRepository.obtenerPorId("o-1").orElseThrow().getImagenes());
    }

    @Test
    void otroVendedorNoPuedeEditarUnaOfertaAjena() {
        // Act & Assert: se trata como inexistente y no cambia nada
        assertThrows(RecursoNoEncontradoException.class,
                () -> editarOferta.ejecutar("o-1", "v-9", "Robada", precioNuevo, 1, Plataforma.PC, null));
        assertEquals("Hades", ofertaRepository.obtenerPorId("o-1").orElseThrow().getTitulo());
    }

    @Test
    void noDebeEditarUnaOfertaQueNoExiste() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> editarOferta.ejecutar("no-existe", "v-1", "X", precioNuevo, 1, Plataforma.PC, null));
    }

    @Test
    void unaImagenInvalidaNoDejaCambiosParcialesEnLaOferta() {
        // Act & Assert: el repositorio en memoria comparte el objeto, así que validamos que la galería no cambie
        assertThrows(ReglaDominioException.class,
                () -> editarOferta.ejecutar("o-1", "v-1", "Hades II", precioNuevo, 9, Plataforma.PC, List.of("javascript:alert(1)")));
        assertEquals(List.of("https://img.test/vieja.png"), ofertaRepository.obtenerPorId("o-1").orElseThrow().getImagenes());
    }
}