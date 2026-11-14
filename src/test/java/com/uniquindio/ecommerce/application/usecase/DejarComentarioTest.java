package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.usecase.ConsultarComentariosDeOferta.ResumenComentarios;
import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
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
    private final DejarComentario dejarComentario = new DejarComentario(comentarioRepository, compraRepository);
    private final ConsultarComentariosDeOferta consultarComentarios = new ConsultarComentariosDeOferta(comentarioRepository);

    private void guardarCompra(String id, String compradorId, String ofertaId, EstadoCompra estado) {
        compraRepository.guardar(Compra.reconstruir(id, ofertaId, compradorId,
                new Precio(new BigDecimal("30"), "USD"), LocalDateTime.now(), estado));
    }

    @Test
    void quienTieneUnaCompraCompletadaPuedeComentarYCalificar() {
        // Arrange
        guardarCompra("c-1", "u-1", "o-1", EstadoCompra.COMPLETADA);

        // Act
        Comentario comentario = dejarComentario.ejecutar("u-1", "o-1", "Excelente juego", 5);

        // Assert
        Comentario guardado = comentarioRepository.obtenerPorId(comentario.getId()).orElseThrow();
        assertEquals("c-1", guardado.getCompraId());
        assertEquals(5, guardado.getCalificacion());
        assertEquals("Excelente juego", guardado.getTexto());
    }

    @Test
    void noDebeComentarQuienSoloTieneUnaCompraPendiente() {
        // Arrange
        guardarCompra("c-2", "u-1", "o-1", EstadoCompra.PENDIENTE);

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> dejarComentario.ejecutar("u-1", "o-1", "Me parece bueno", 4));

        // Assert
        assertEquals("El usuario debe haber comprado la oferta para dejar un comentario.", excepcion.getMessage());
        assertTrue(comentarioRepository.buscarPorOfertaId("o-1").isEmpty());
    }

    @Test
    void noDebePermitirDosComentariosSobreLaMismaCompra() {
        // Arrange
        guardarCompra("c-3", "u-1", "o-1", EstadoCompra.COMPLETADA);
        dejarComentario.ejecutar("u-1", "o-1", "Primer comentario", 5);

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> dejarComentario.ejecutar("u-1", "o-1", "Segundo comentario", 1));

        // Assert
        assertEquals("Ya comentaste esta compra", excepcion.getMessage());
        assertEquals(1, comentarioRepository.buscarPorOfertaId("o-1").size());
    }

    @Test
    void noDebeGuardarCalificacionesFueraDeRangoNiTextosInvalidos() {
        // Arrange
        guardarCompra("c-4", "u-1", "o-1", EstadoCompra.COMPLETADA);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> dejarComentario.ejecutar("u-1", "o-1", "Texto", 0));
        assertThrows(ReglaDominioException.class, () -> dejarComentario.ejecutar("u-1", "o-1", "Texto", 6));
        assertThrows(ReglaDominioException.class, () -> dejarComentario.ejecutar("u-1", "o-1", "  ", 4));
        assertThrows(ReglaDominioException.class, () -> dejarComentario.ejecutar("u-1", "o-1", "a".repeat(501), 4));
        assertTrue(comentarioRepository.buscarPorOfertaId("o-1").isEmpty());
    }

    @Test
    void deberiaListarElMasRecientePrimeroYCalcularElPromedio() {
        // Arrange
        comentarioRepository.guardar(Comentario.reconstruir("m-1", "u-1", "o-1", "c-1", "Antiguo", 5,
                LocalDateTime.now().minusDays(2), null, null));
        comentarioRepository.guardar(Comentario.reconstruir("m-2", "u-2", "o-1", "c-2", "Reciente", 3,
                LocalDateTime.now().minusHours(1), null, null));
        comentarioRepository.guardar(Comentario.reconstruir("m-3", "u-3", "o-2", "c-3", "De otra oferta", 1,
                LocalDateTime.now(), null, null));

        // Act
        ResumenComentarios resumen = consultarComentarios.ejecutar("o-1");

        // Assert
        List<String> ids = resumen.comentarios().stream().map(Comentario::getId).toList();
        assertEquals(List.of("m-2", "m-1"), ids);
        assertEquals(4.0, resumen.promedioCalificacion());
    }

    @Test
    void elPromedioEsCeroCuandoLaOfertaNoTieneComentarios() {
        // Act
        ResumenComentarios resumen = consultarComentarios.ejecutar("o-sin-comentarios");

        // Assert
        assertEquals(0.0, resumen.promedioCalificacion());
        assertTrue(resumen.comentarios().isEmpty());
    }
}