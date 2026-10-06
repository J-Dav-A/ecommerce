package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompraTest {

    @Test
    void dosComprasConElMismoIdSonLaMismaAunqueTenganDatosDistintos() {
        // Arrange
        String mismoId = UUID.randomUUID().toString();
        Compra compra1 = Compra.realizar(mismoId, "oferta-1", "comprador-1",
                new Precio(new BigDecimal("10")));
        Compra compra2 = Compra.realizar(mismoId, "oferta-2", "comprador-2",
                new Precio(new BigDecimal("99")));

        // Act & Assert
        assertEquals(compra1, compra2);
    }

    @Test
    void noDebeReembolsarUnaCompraPendienteYElEstadoNoDebeCambiar() {
        // Arrange
        Compra compra = Compra.realizar(UUID.randomUUID().toString(), "oferta-1",
                "comprador-1", new Precio(new BigDecimal("50")));

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> compra.solicitarReembolso("No llegó la clave"));
        assertEquals(EstadoCompra.PENDIENTE, compra.getEstado());
    }
}
