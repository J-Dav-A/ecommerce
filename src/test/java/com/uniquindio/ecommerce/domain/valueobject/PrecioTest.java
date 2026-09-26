package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrecioTest {

    @Test
    void dosPreciosConElMismoValorDebenSerIguales() {
        // Arrange
        Precio p1 = new Precio(new BigDecimal("59.99"));
        Precio p2 = new Precio(new BigDecimal("59.99"));

        // Act & Assert
        assertEquals(p1, p2);
    }

    @Test
    void noDebePermitirCrearUnPrecioMenorOIgualACero() {
        // Arrange
        BigDecimal valorInvalido = BigDecimal.ZERO;

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> new Precio(valorInvalido));
    }
}
