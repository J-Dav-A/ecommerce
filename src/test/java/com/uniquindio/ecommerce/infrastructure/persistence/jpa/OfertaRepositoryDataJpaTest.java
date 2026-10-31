package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.OfertaPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

// @DataJpaTest solo levanta la capa de persistencia (entidades + interfaces JPA) con una H2 embebida.
// NO carga nuestro adaptador (OfertaRepositoryJpa) ni el mapper: por eso se traen con @Import.
// Las pruebas crean sus propios datos (títulos únicos) para no depender del data.sql.
@DataJpaTest
@Import({OfertaRepositoryJpa.class, OfertaPersistenceMapper.class})
class OfertaRepositoryDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Test
    void deberiaBuscarPorTextoYPaginarOrdenadoPorTitulo() {
        // Arrange
        entityManager.persist(crearOferta("dj-1", "Hollow Knight: Silksong", EstadoOferta.PUBLICADA));
        entityManager.persist(crearOferta("dj-2", "Celeste", EstadoOferta.PUBLICADA));
        entityManager.persist(crearOferta("dj-3", "Hollow Knight", EstadoOferta.PUBLICADA));
        entityManager.flush();

        // Act
        Pagina<Oferta> pagina = ofertaRepository.buscar("hollow", null, 0, 1);

        // Assert: 2 coinciden con "hollow" (sin importar mayúsculas), de a 1 por página -> 2 páginas
        assertEquals(2, pagina.totalElementos());
        assertEquals(2, pagina.totalPaginas());
        assertEquals("Hollow Knight", pagina.contenido().get(0).getTitulo());
    }

    @Test
    void noDeberiaDevolverLasOfertasEliminadas() {
        // Arrange
        entityManager.persist(crearOferta("dj-4", "Hades", EstadoOferta.PUBLICADA));
        entityManager.persist(crearOferta("dj-5", "Hades II", EstadoOferta.ELIMINADA));
        entityManager.flush();

        // Act
        Pagina<Oferta> pagina = ofertaRepository.buscar("hades", null, 0, 10);

        // Assert: solo aparece la publicada
        assertEquals(1, pagina.totalElementos());
        assertEquals("Hades", pagina.contenido().get(0).getTitulo());
    }

    private OfertaEntity crearOferta(String id, String titulo, EstadoOferta estado) {
        OfertaEntity oferta = new OfertaEntity();
        oferta.setId(id);
        oferta.setVendedorId("u-1");
        oferta.setTitulo(titulo);
        oferta.setPrecio(new PrecioEmbeddable(new BigDecimal("29.99"), "USD"));
        oferta.setStock(5);
        oferta.setPlataforma(Plataforma.PC);
        oferta.setEstado(estado);
        return oferta;
    }
}