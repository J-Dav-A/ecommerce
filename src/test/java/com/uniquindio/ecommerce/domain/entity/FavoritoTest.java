package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FavoritoTest {

    @Test
    void deberiaCrearUnFavoritoConSusReferencias() {
        // Act
        Favorito favorito = Favorito.crear("f-1", "u-1", "o-1", LocalDateTime.now());

        // Assert
        assertEquals("u-1", favorito.getCompradorId());
        assertEquals("o-1", favorito.getOfertaId());
    }

    @Test
    void noDebeCrearseSinComprador_Oferta_NiFecha() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> Favorito.crear("f-1", " ", "o-1", LocalDateTime.now()));
        assertThrows(ReglaDominioException.class, () -> Favorito.crear("f-1", "u-1", null, LocalDateTime.now()));
        assertThrows(ReglaDominioException.class, () -> Favorito.crear("f-1", "u-1", "o-1", null));
    }
}