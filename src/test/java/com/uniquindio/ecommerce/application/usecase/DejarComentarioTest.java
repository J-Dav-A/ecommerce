package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.service.VerificadorDeCompra;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ComentarioRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DejarComentarioTest {

    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ComentarioRepository comentarioRepository = new ComentarioRepositoryEnMemoria();
    private final DejarComentario dejarComentario =
            new DejarComentario(comentarioRepository, new VerificadorDeCompra(compraRepository));

    private void guardarCompraCompletada(String compradorId, String ofertaId) {
        compraRepository.guardar(Compra.reconstruir("c-" + compradorId + ofertaId, ofertaId, compradorId,
                new Precio(new BigDecimal("30"), "USD"), LocalDateTime.now(), EstadoCompra.COMPLETADA));
    }

    @Test
    void quienCompraLaOfertaPuedeComentarla() {
        // Arrange
        guardarCompraCompletada("u-1", "o-1");

        // Act
        Comentario comentario = dejarComentario.ejecutar("u-1", "o-1", "Excelente juego");

        // Assert
        List<Comentario> guardados = comentarioRepository.buscarPorOfertaId("o-1");
        assertEquals(1, guardados.size());
        assertEquals(comentario.getId(), guardados.get(0).getId());
    }

    @Test
    void noDebeComentarQuienNoHaCompradoLaOferta() {
        // Arrange: u-2 no tiene ninguna compra completada de o-1

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> dejarComentario.ejecutar("u-2", "o-1", "Me parece bueno"));

        // Assert
        assertEquals("El usuario debe haber comprado la oferta para dejar un comentario.", excepcion.getMessage());
        assertTrue(comentarioRepository.buscarPorOfertaId("o-1").isEmpty());
    }

    @Test
    void noDebeGuardarUnComentarioVacio() {
        // Arrange
        guardarCompraCompletada("u-1", "o-1");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> dejarComentario.ejecutar("u-1", "o-1", "  "));
        assertTrue(comentarioRepository.buscarPorOfertaId("o-1").isEmpty());
    }

    @Test
    void deberiaListarSoloLosComentariosDeLaOfertaPedida() {
        // Arrange
        guardarCompraCompletada("u-1", "o-1");
        guardarCompraCompletada("u-1", "o-2");
        dejarComentario.ejecutar("u-1", "o-1", "Comentario de la oferta 1");
        dejarComentario.ejecutar("u-1", "o-2", "Comentario de la oferta 2");

        // Act
        List<Comentario> comentarios = dejarComentario.listarPorOferta("o-1");

        // Assert
        assertEquals(1, comentarios.size());
        assertEquals("Comentario de la oferta 1", comentarios.get(0).getTexto());
    }
}