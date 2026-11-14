package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CargarClaveDigitalTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final ClaveDigitalRepository claveRepository = new ClaveDigitalRepositoryEnMemoria();
    private final CargarClaveDigital cargarClave = new CargarClaveDigital(ofertaRepository, claveRepository);

    private void guardarOferta() {
        ofertaRepository.guardar(Oferta.crear("o-1", "v-1", "Hades II", new Precio(new BigDecimal("30"), "USD"), 5));
    }

    @Test
    void elDuenoDeLaOfertaPuedeCargarleUnaClaveDisponible() {
        // Arrange
        guardarOferta();

        // Act
        ClaveDigital clave = cargarClave.ejecutar("o-1", "v-1", "AAAA-BBBB-CCCC", 1, false);

        // Assert
        ClaveDigital guardada = claveRepository.obtenerPorId(clave.getId()).orElseThrow();
        assertEquals("AAAA-BBBB-CCCC", guardada.getCodigo());
        assertEquals(EstadoClaveDigital.DISPONIBLE, guardada.getEstado());
        assertEquals("o-1", guardada.getOfertaId());
    }

    @Test
    void noDebePermitirCargarClavesALaOfertaDeOtroVendedor() {
        // Arrange
        guardarOferta();

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> cargarClave.ejecutar("o-1", "v-9", "AAAA-BBBB-CCCC", 1, false));
        assertTrue(claveRepository.obtenerDisponiblePorOferta("o-1").isEmpty());
    }

    @Test
    void noDebeCargarClavesAUnaOfertaQueNoExiste() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> cargarClave.ejecutar("no-existe", "v-1", "AAAA", 1, false));
    }

    @Test
    void noDebeCargarUnaClaveSinCodigoNiConLicenciaInvalida() {
        // Arrange
        guardarOferta();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> cargarClave.ejecutar("o-1", "v-1", " ", 1, false));
        assertThrows(ReglaDominioException.class, () -> cargarClave.ejecutar("o-1", "v-1", "AAAA", 0, false));
        assertTrue(claveRepository.obtenerDisponiblePorOferta("o-1").isEmpty());
    }
}