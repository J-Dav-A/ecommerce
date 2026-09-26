package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClaveDigitalTest {

    @Test
    void dosClavesConElMismoIdSonLaMismaAunqueTenganDatosDistintos() {
        // Arrange
        UUID mismoId = UUID.randomUUID();
        ClaveDigital clave1 = new ClaveDigital(mismoId, "AAAA-1111", new LicenciaDigital(1, false));
        ClaveDigital clave2 = new ClaveDigital(mismoId, "BBBB-2222", new LicenciaDigital(3, true));

        // Act & Assert
        assertEquals(clave1, clave2);
    }

    @Test
    void noDebePermitirAsignarUnaClaveQueYaFueAsignada() {
        // Arrange
        ClaveDigital clave = new ClaveDigital(UUID.randomUUID(), "CCCC-3333", new LicenciaDigital(1, false));
        clave.asignar(UUID.randomUUID(), Instant.now());

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> clave.asignar(UUID.randomUUID(), Instant.now()));
    }

    //Prueba de invariante del agregado ClaveDigital
    @Test
    void noDebeReembolsarUnaClaveCanjeadaYElEstadoNoDebeCambiar() {
        // Arrange
        ClaveDigital clave = new ClaveDigital(UUID.randomUUID(), "DDDD-4444", new LicenciaDigital(1, false));
        clave.asignar(UUID.randomUUID(), Instant.now());
        clave.canjear();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> clave.reembolsar(Instant.now(), Duration.ofDays(7)));
        assertEquals(EstadoClaveDigital.CANJEADA, clave.getEstado());
    }
}
