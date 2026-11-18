package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComentarioTest {

    private Comentario crear(String texto, int calificacion) {
        return Comentario.crear("m-1", "u-1", "o-1", "c-1", texto, calificacion, LocalDateTime.now());
    }

    @Test
    void deberiaCrearUnComentarioConCalificacionYSinRespuesta() {
        // Act
        Comentario comentario = crear("Excelente juego", 5);

        // Assert
        assertEquals(5, comentario.getCalificacion());
        assertEquals("c-1", comentario.getCompraId());
        assertFalse(comentario.estaRespondido());
    }

    @Test
    void laCalificacionDebeEstarEntre1Y5() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> crear("Texto", 0));
        assertThrows(ReglaDominioException.class, () -> crear("Texto", 6));
        assertEquals(1, crear("Texto", 1).getCalificacion());
        assertEquals(5, crear("Texto", 5).getCalificacion());
    }

    @Test
    void elTextoNoPuedeEstarVacioNiSuperar500Caracteres() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> crear("   ", 4));
        assertThrows(ReglaDominioException.class, () -> crear("a".repeat(501), 4));
        assertEquals(500, crear("a".repeat(500), 4).getTexto().length());
    }

    @Test
    void elVendedorPuedeResponderUnaVezYQuedaRegistrada() {
        // Arrange
        Comentario comentario = crear("Buen juego", 4);

        // Act
        comentario.responder("Gracias por tu compra", LocalDateTime.now());

        // Assert
        assertTrue(comentario.estaRespondido());
        assertEquals("Gracias por tu compra", comentario.getRespuesta());
        assertThrows(ReglaDominioException.class, () -> comentario.responder("Otra respuesta", LocalDateTime.now()));
        assertEquals("Gracias por tu compra", comentario.getRespuesta());
    }

    @Test
    void laRespuestaNoPuedeEstarVaciaNiSuperar500Caracteres() {
        // Arrange
        Comentario comentario = crear("Buen juego", 4);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> comentario.responder(" ", LocalDateTime.now()));
        assertThrows(ReglaDominioException.class, () -> comentario.responder("a".repeat(501), LocalDateTime.now()));
        assertFalse(comentario.estaRespondido());
    }
}