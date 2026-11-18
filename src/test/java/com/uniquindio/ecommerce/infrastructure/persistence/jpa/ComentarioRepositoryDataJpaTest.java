package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.ComentarioPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Igual que en OfertaRepositoryDataJpaTest: @DataJpaTest no carga el adaptador ni el mapper, por eso van en @Import.
@DataJpaTest
@Import({ComentarioRepositoryJpa.class, ComentarioPersistenceMapper.class})
class ComentarioRepositoryDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Test
    void deberiaGuardarYRecuperarUnComentarioConCalificacionYRespuesta() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 1, 10, 30);
        Comentario comentario = Comentario.crear("m-1", "u-2", "o-101", "c-1", "Excelente juego", 5, fecha);
        comentario.responder("Gracias por tu compra", fecha.plusHours(3));

        // Act
        comentarioRepository.guardar(comentario);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Comentario recuperado = comentarioRepository.obtenerPorId("m-1").orElseThrow();
        assertEquals(5, recuperado.getCalificacion());
        assertEquals("c-1", recuperado.getCompraId());
        assertEquals(fecha, recuperado.getFecha());
        assertEquals("Gracias por tu compra", recuperado.getRespuesta());
    }

    @Test
    void deberiaListarPorOfertaConElMasRecientePrimero() {
        // Arrange
        comentarioRepository.guardar(Comentario.crear("m-1", "u-1", "o-101", "c-1", "Antiguo", 4,
                LocalDateTime.of(2026, 10, 1, 9, 0)));
        comentarioRepository.guardar(Comentario.crear("m-2", "u-2", "o-101", "c-2", "Reciente", 2,
                LocalDateTime.of(2026, 11, 1, 9, 0)));
        comentarioRepository.guardar(Comentario.crear("m-3", "u-3", "o-102", "c-3", "De otra oferta", 5,
                LocalDateTime.of(2026, 11, 2, 9, 0)));
        entityManager.flush();
        entityManager.clear();

        // Act
        List<Comentario> comentarios = comentarioRepository.buscarPorOfertaId("o-101");

        // Assert
        assertEquals(List.of("m-2", "m-1"), comentarios.stream().map(Comentario::getId).toList());
    }

    @Test
    void deberiaSaberSiUnaCompraYaTieneComentario() {
        // Arrange
        comentarioRepository.guardar(Comentario.crear("m-1", "u-1", "o-101", "c-1", "Texto", 3, LocalDateTime.now()));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertTrue(comentarioRepository.existePorCompraId("c-1"));
        assertFalse(comentarioRepository.existePorCompraId("c-2"));
    }

    @Test
    void deberiaCalcularPromedioYTotalPorOfertaYOmitirLasQueNoTienenComentarios() {
        // Arrange
        comentarioRepository.guardar(Comentario.crear("m-1", "u-1", "o-101", "c-1", "A", 5, LocalDateTime.now()));
        comentarioRepository.guardar(Comentario.crear("m-2", "u-2", "o-101", "c-2", "B", 2, LocalDateTime.now()));
        comentarioRepository.guardar(Comentario.crear("m-3", "u-3", "o-102", "c-3", "C", 4, LocalDateTime.now()));
        entityManager.flush();
        entityManager.clear();

        // Act
        List<ResumenPorOferta> resumenes = comentarioRepository.resumenPorOfertas(List.of("o-101", "o-102", "o-sin"));

        // Assert
        assertEquals(2, resumenes.size());
        ResumenPorOferta o101 = resumenes.stream().filter(r -> r.ofertaId().equals("o-101")).findFirst().orElseThrow();
        assertEquals(3.5, o101.promedio());
        assertEquals(2L, o101.total());
        assertTrue(comentarioRepository.resumenPorOfertas(List.of()).isEmpty());
    }
}