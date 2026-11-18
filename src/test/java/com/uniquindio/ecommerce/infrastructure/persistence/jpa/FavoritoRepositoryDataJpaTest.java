package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.FavoritoPersistenceMapper;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({FavoritoRepositoryJpa.class, FavoritoPersistenceMapper.class})
class FavoritoRepositoryDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Test
    void deberiaGuardarConsultarYListarPorCompradorConElMasRecientePrimero() {
        // Arrange
        favoritoRepository.guardar(Favorito.crear("f-1", "u-9", "o-101", LocalDateTime.of(2026, 10, 1, 9, 0)));
        favoritoRepository.guardar(Favorito.crear("f-2", "u-9", "o-102", LocalDateTime.of(2026, 11, 1, 9, 0)));
        favoritoRepository.guardar(Favorito.crear("f-3", "u-8", "o-101", LocalDateTime.of(2026, 11, 2, 9, 0)));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertTrue(favoritoRepository.existe("u-9", "o-101"));
        assertFalse(favoritoRepository.existe("u-9", "otra"));
        List<Favorito> favoritos = favoritoRepository.obtenerPorCompradorId("u-9");
        assertEquals(List.of("o-102", "o-101"), favoritos.stream().map(Favorito::getOfertaId).toList());
    }

    @Test
    void deberiaEliminarSoloElFavoritoIndicado() {
        // Arrange
        favoritoRepository.guardar(Favorito.crear("f-1", "u-9", "o-101", LocalDateTime.now()));
        favoritoRepository.guardar(Favorito.crear("f-2", "u-9", "o-102", LocalDateTime.now()));
        entityManager.flush();

        // Act & Assert
        assertTrue(favoritoRepository.eliminar("u-9", "o-101"));
        assertFalse(favoritoRepository.eliminar("u-9", "o-101"));
        assertEquals(1, favoritoRepository.obtenerPorCompradorId("u-9").size());
    }

    @Test
    void laBaseDeDatosImpideDuplicarElMismoParCompradorOferta() {
        // Arrange
        favoritoRepository.guardar(Favorito.crear("f-1", "u-9", "o-101", LocalDateTime.now()));
        entityManager.flush();

        // Act & Assert: con otro id pero el mismo par, la restricción única lo rechaza
        favoritoRepository.guardar(Favorito.crear("f-2", "u-9", "o-101", LocalDateTime.now()));
        assertThrows(PersistenceException.class, () -> entityManager.flush());
    }
}