package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LicenciaDigitalTest {

    @Test
    void dosLicenciasConLosMismosTerminosDebenSerIguales() {
        // Arrange
        LicenciaDigital l1 = new LicenciaDigital(1, false);
        LicenciaDigital l2 = new LicenciaDigital(1, false);

        // Act & Assert
        assertEquals(l1, l2);
    }

    @Test
    void noDebePermitirCrearUnaLicenciaConCeroActivaciones() {
        // Act & Assert -> el Arrange no es necesario porque se pone directamente en el assertThrows
        assertThrows(ReglaDominioException.class, () -> new LicenciaDigital(0, false));
    }
}
