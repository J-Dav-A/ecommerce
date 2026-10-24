package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfertaTest {

    @Test
    void dosOfertasConElMismoIdSonLaMismaAunqueTenganDatosDistintos() {
        // Arrange
        String mismoId = UUID.randomUUID().toString();
        Oferta oferta1 = Oferta.crear(mismoId, UUID.randomUUID().toString(), "Juego A", new Precio(new BigDecimal("10"), "COP"), 5);
        Oferta oferta2 = Oferta.crear(mismoId, UUID.randomUUID().toString(), "Juego B", new Precio(new BigDecimal("99"), "COP"), 1);

        // Act & Assert
        assertEquals(oferta1, oferta2);
    }

    @Test
    void noDebePermitirDescontarMasStockDelDisponible() {
        // Arrange
        Oferta oferta = Oferta.crear(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "Juego A", new Precio(new BigDecimal("10"), "COP"), 2);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> oferta.descontarStock(5));
    }

    //Prueba de invariante del agregado Oferta
    @Test
    void noDebePublicarSinPlataformaYElEstadoNoDebeCambiar() {
        // Arrange
        Oferta oferta = Oferta.crear(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "Juego A", new Precio(new BigDecimal("10"), "COP"), 5);

        // Act & Assert
        assertThrows(ReglaDominioException.class, oferta::publicar);
        assertEquals(EstadoOferta.BORRADOR, oferta.getEstado());
    }
}