package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsignarClaveDigitalTest {

    private final ClaveDigitalRepository claveRepository = new ClaveDigitalRepositoryEnMemoria();
    private final AsignarClaveDigital asignarClave = new AsignarClaveDigital(claveRepository);

    @Test
    void deberiaAsignarLaClaveAlCompradorYGuardarla() {
        // Arrange
        claveRepository.guardar(ClaveDigital.crear("k-1", "o-1", "AAAA-BBBB", new LicenciaDigital(1, false)));

        // Act
        asignarClave.ejecutar("k-1", "u-1");

        // Assert
        ClaveDigital guardada = claveRepository.obtenerPorId("k-1").orElseThrow();
        assertEquals(EstadoClaveDigital.ASIGNADA, guardada.getEstado());
        assertEquals("u-1", guardada.getCompradorId());
    }

    @Test
    void noDebeAsignarUnaClaveQueYaFueAsignada() {
        // Arrange
        claveRepository.guardar(ClaveDigital.crear("k-2", "o-1", "CCCC-DDDD", new LicenciaDigital(1, false)));
        asignarClave.ejecutar("k-2", "u-1");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> asignarClave.ejecutar("k-2", "u-2"));
        assertEquals("u-1", claveRepository.obtenerPorId("k-2").orElseThrow().getCompradorId());
    }

    @Test
    void noDebeAsignarUnaClaveQueNoExiste() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> asignarClave.ejecutar("no-existe", "u-1"));
    }
}