package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ComentarioRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponderComentarioTest {

    private final ComentarioRepository comentarioRepository = new ComentarioRepositoryEnMemoria();
    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final ResponderComentario responderComentario = new ResponderComentario(comentarioRepository, ofertaRepository);

    @BeforeEach
    void preparar() {
        ofertaRepository.guardar(Oferta.crear("o-1", "v-1", "Hades II", new Precio(new BigDecimal("30"), "USD"), 5));
        comentarioRepository.guardar(Comentario.crear("m-1", "u-1", "o-1", "c-1", "Muy bueno", 5, LocalDateTime.now()));
    }

    @Test
    void elVendedorDuenoDeLaOfertaPuedeResponder() {
        // Act
        responderComentario.ejecutar("m-1", "v-1", "Gracias por tu compra");

        // Assert
        Comentario guardado = comentarioRepository.obtenerPorId("m-1").orElseThrow();
        assertEquals("Gracias por tu compra", guardado.getRespuesta());
    }

    @Test
    void otroVendedorNoPuedeResponderElComentarioDeUnaOfertaAjena() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> responderComentario.ejecutar("m-1", "v-9", "Intruso"));
        assertNull(comentarioRepository.obtenerPorId("m-1").orElseThrow().getRespuesta());
    }

    @Test
    void noDebeResponderUnComentarioQueNoExiste() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> responderComentario.ejecutar("no-existe", "v-1", "Hola"));
    }

    @Test
    void noDebePermitirResponderDosVeces() {
        // Arrange
        responderComentario.ejecutar("m-1", "v-1", "Primera respuesta");

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> responderComentario.ejecutar("m-1", "v-1", "Segunda respuesta"));

        // Assert
        assertEquals("El comentario ya fue respondido", excepcion.getMessage());
        assertEquals("Primera respuesta", comentarioRepository.obtenerPorId("m-1").orElseThrow().getRespuesta());
    }
}