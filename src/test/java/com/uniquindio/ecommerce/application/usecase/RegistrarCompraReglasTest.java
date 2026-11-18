package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarCompraReglasTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ClaveDigitalRepository claveRepository = new ClaveDigitalRepositoryEnMemoria();
    private final RegistrarCompra registrarCompra =
            new RegistrarCompra(ofertaRepository, compraRepository, claveRepository);

    private Oferta guardarOfertaPublicada(String id) {
        Oferta oferta = Oferta.crear(id, "v-1", "Juego " + id, new Precio(new BigDecimal("30"), "USD"), 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        ofertaRepository.guardar(oferta);
        return oferta;
    }

    private void guardarClave(String id, String ofertaId) {
        claveRepository.guardar(ClaveDigital.crear(id, ofertaId, "CODIGO-" + id, new LicenciaDigital(1, false)));
    }

    @Test
    void noDebePermitirComprarDosVecesLaMismaOferta() {
        // Arrange
        guardarOfertaPublicada("o-1");
        guardarClave("k-1", "o-1");
        guardarClave("k-2", "o-1");
        registrarCompra.ejecutar("o-1", "u-1");

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> registrarCompra.ejecutar("o-1", "u-1"));

        // Assert: solo se consumió una clave y quedó una sola compra
        assertEquals("Ya compraste esta oferta", excepcion.getMessage());
        assertEquals(1, compraRepository.obtenerPorCompradorId("u-1").size());
    }

    @Test
    void otroCompradorSiPuedeComprarLaMismaOferta() {
        // Arrange
        guardarOfertaPublicada("o-2");
        guardarClave("k-3", "o-2");
        guardarClave("k-4", "o-2");
        registrarCompra.ejecutar("o-2", "u-1");

        // Act
        registrarCompra.ejecutar("o-2", "u-2");

        // Assert
        assertEquals(1, compraRepository.obtenerPorCompradorId("u-2").size());
    }

    @Test
    void noDebeVenderUnaOfertaEliminada() {
        // Arrange
        Oferta oferta = guardarOfertaPublicada("o-3");
        guardarClave("k-5", "o-3");
        oferta.eliminar();
        ofertaRepository.guardar(oferta);

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> registrarCompra.ejecutar("o-3", "u-1"));

        // Assert
        assertEquals("La oferta no está disponible para la venta", excepcion.getMessage());
        assertEquals(0, compraRepository.obtenerPorCompradorId("u-1").size());
    }
}