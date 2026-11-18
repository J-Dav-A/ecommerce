package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuscarOfertasTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final BuscarOfertas buscarOfertas = new BuscarOfertas(ofertaRepository);

    private void guardarOfertaPublicada(String id, String titulo, Plataforma plataforma) {
        Oferta oferta = Oferta.crear(id, "u-1", titulo, new Precio(new BigDecimal("10"), "USD"), 5);
        oferta.definirPlataforma(plataforma);
        oferta.publicar();
        ofertaRepository.guardar(oferta);
    }

    @Test
    void deberiaBuscarPorTextoYPlataforma() {
        // Arrange
        guardarOfertaPublicada("o-1", "Hollow Knight", Plataforma.PC);
        guardarOfertaPublicada("o-2", "Hollow Knight Silksong", Plataforma.NINTENDO);
        guardarOfertaPublicada("o-3", "Celeste", Plataforma.PC);

        // Act
        Pagina<Oferta> pagina = buscarOfertas.buscar("hollow", Plataforma.PC, 0, 10);

        // Assert
        assertEquals(1, pagina.totalElementos());
        assertEquals("o-1", pagina.contenido().get(0).getId());
    }

    @Test
    void noDebeAceptarUnaPaginaNegativa() {
        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> buscarOfertas.buscar(null, null, -1, 10));

        // Assert
        assertEquals("La página no puede ser negativa", excepcion.getMessage());
    }

    @Test
    void noDebeAceptarUnTamanoDePaginaFueraDelRango() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> buscarOfertas.buscar(null, null, 0, 0));
        assertThrows(ReglaDominioException.class, () -> buscarOfertas.buscar(null, null, 0, 51));
    }

    @Test
    void noDebeAceptarUnLimiteDeRankingFueraDelRango() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> buscarOfertas.masVendidas(0));
        assertThrows(ReglaDominioException.class, () -> buscarOfertas.masVendidas(21));
    }

    @Test
    void deberiaResponderNoEncontradaCuandoLaOfertaNoExiste() {
        // Act
        RecursoNoEncontradoException excepcion = assertThrows(RecursoNoEncontradoException.class,
                () -> buscarOfertas.obtener("no-existe"));

        // Assert
        assertEquals("La oferta no existe", excepcion.getMessage());
    }
}