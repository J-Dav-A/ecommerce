package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.FavoritoRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestionDeFavoritosTest {

    private final FavoritoRepository favoritoRepository = new FavoritoRepositoryEnMemoria();
    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final AgregarFavorito agregarFavorito = new AgregarFavorito(favoritoRepository, ofertaRepository);
    private final QuitarFavorito quitarFavorito = new QuitarFavorito(favoritoRepository);
    private final ListarFavoritos listarFavoritos = new ListarFavoritos(favoritoRepository, ofertaRepository);

    private final Precio precio = new Precio(new BigDecimal("30"), "USD");

    private Oferta publicada(String id) {
        Oferta oferta = Oferta.crear(id, "v-1", "Juego " + id, precio, 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        ofertaRepository.guardar(oferta);
        return oferta;
    }

    @BeforeEach
    void preparar() {
        publicada("o-1");
        publicada("o-2");
        ofertaRepository.guardar(Oferta.crear("o-borrador", "v-1", "Sin publicar", precio, 5));
    }

    @Test
    void deberiaAgregarUnaOfertaPublicadaALosFavoritos() {
        // Act
        Oferta oferta = agregarFavorito.ejecutar("u-1", "o-1");

        // Assert
        assertEquals("o-1", oferta.getId());
        assertTrue(favoritoRepository.existe("u-1", "o-1"));
        assertFalse(favoritoRepository.existe("u-2", "o-1"));
    }

    @Test
    void noDebeAgregarDosVecesLaMismaOferta() {
        // Arrange
        agregarFavorito.ejecutar("u-1", "o-1");

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> agregarFavorito.ejecutar("u-1", "o-1"));

        // Assert
        assertEquals("La oferta ya está en tus favoritos", excepcion.getMessage());
        assertEquals(1, favoritoRepository.obtenerPorCompradorId("u-1").size());
    }

    @Test
    void dosCompradoresPuedenTenerLaMismaOfertaComoFavorita() {
        // Act
        agregarFavorito.ejecutar("u-1", "o-1");
        agregarFavorito.ejecutar("u-2", "o-1");

        // Assert
        assertEquals(1, favoritoRepository.obtenerPorCompradorId("u-1").size());
        assertEquals(1, favoritoRepository.obtenerPorCompradorId("u-2").size());
    }

    @Test
    void noDebeAgregarOfertasInexistentesNiSinPublicar() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> agregarFavorito.ejecutar("u-1", "no-existe"));
        assertThrows(RecursoNoEncontradoException.class, () -> agregarFavorito.ejecutar("u-1", "o-borrador"));
        assertTrue(favoritoRepository.obtenerPorCompradorId("u-1").isEmpty());
    }

    @Test
    void deberiaQuitarUnFavoritoYFallarSiNoEstaba() {
        // Arrange
        agregarFavorito.ejecutar("u-1", "o-1");

        // Act
        quitarFavorito.ejecutar("u-1", "o-1");

        // Assert
        assertFalse(favoritoRepository.existe("u-1", "o-1"));
        assertThrows(RecursoNoEncontradoException.class, () -> quitarFavorito.ejecutar("u-1", "o-1"));
        // Quitar el favorito de otro comprador no existe para este usuario
        agregarFavorito.ejecutar("u-2", "o-2");
        assertThrows(RecursoNoEncontradoException.class, () -> quitarFavorito.ejecutar("u-1", "o-2"));
        assertTrue(favoritoRepository.existe("u-2", "o-2"));
    }

    @Test
    void deberiaListarLosFavoritosConElMasRecientePrimeroYOcultarLasOfertasEliminadas() {
        // Arrange: se guardan con fechas distintas para fijar el orden
        favoritoRepository.guardar(Favorito.crear("f-1", "u-1", "o-1", LocalDateTime.now().minusDays(2)));
        favoritoRepository.guardar(Favorito.crear("f-2", "u-1", "o-2", LocalDateTime.now().minusDays(1)));

        // Act & Assert
        assertEquals(List.of("o-2", "o-1"), listarFavoritos.ejecutar("u-1").stream().map(Oferta::getId).toList());

        // El vendedor elimina o-2: deja de aparecer en la lista
        Oferta eliminada = ofertaRepository.obtenerPorId("o-2").orElseThrow();
        eliminada.eliminar();
        ofertaRepository.guardar(eliminada);
        assertEquals(List.of("o-1"), listarFavoritos.ejecutar("u-1").stream().map(Oferta::getId).toList());
    }
}