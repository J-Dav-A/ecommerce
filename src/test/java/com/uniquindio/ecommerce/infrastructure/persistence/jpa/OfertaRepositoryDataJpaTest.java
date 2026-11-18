package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.FiltroBusqueda;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.OrdenOfertas;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ComentarioEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.OfertaPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void deberiaGuardarLasImagenesEnOrdenYReemplazarlasAlActualizar() {
        // Arrange
        Oferta oferta = Oferta.crear("dj-img", "v-1", "Con imágenes",
                new com.uniquindio.ecommerce.domain.valueobject.Precio(new BigDecimal("10"), "USD"), 3);
        oferta.definirImagenes(List.of("https://img.test/1.png", "https://img.test/2.png"));
        ofertaRepository.guardar(oferta);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert: se recuperan en el mismo orden
        Oferta recuperada = ofertaRepository.obtenerPorId("dj-img").orElseThrow();
        assertEquals(List.of("https://img.test/1.png", "https://img.test/2.png"), recuperada.getImagenes());

        // Act & Assert: al reemplazar la galería solo queda la nueva
        recuperada.definirImagenes(List.of("https://img.test/3.png"));
        ofertaRepository.guardar(recuperada);
        entityManager.flush();
        entityManager.clear();
        assertEquals(List.of("https://img.test/3.png"), ofertaRepository.obtenerPorId("dj-img").orElseThrow().getImagenes());
    }

    @Test
    void obtenerTodasPorVendedorIncluyeLasEliminadasPeroObtenerPorVendedorNo() {
        // Arrange
        entityManager.persist(crearOferta("dj-v1", "Activa", EstadoOferta.PUBLICADA));
        entityManager.persist(crearOferta("dj-v2", "Eliminada", EstadoOferta.ELIMINADA));
        entityManager.flush();
        String vendedor = entityManager.find(OfertaEntity.class, "dj-v1").getVendedorId();

        // Act & Assert
        assertTrue(ofertaRepository.obtenerPorVendedorId(vendedor).stream().noneMatch(o -> o.getId().equals("dj-v2")));
        assertTrue(ofertaRepository.obtenerTodasPorVendedorId(vendedor).stream().anyMatch(o -> o.getId().equals("dj-v2")));
    }

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

    @Test
    void deberiaFiltrarPorRangoDePrecioYCalificacionMinima() {
        // Arrange: tres ofertas con el texto "zz-filtro" y distinto precio; solo dos tienen comentarios
        entityManager.persist(ofertaConPrecio("zf-1", "zz-filtro barato", "10"));
        entityManager.persist(ofertaConPrecio("zf-2", "zz-filtro medio", "30"));
        entityManager.persist(ofertaConPrecio("zf-3", "zz-filtro caro", "60"));
        entityManager.persist(comentario("m-1", "zf-1", "c-1", 5));
        entityManager.persist(comentario("m-2", "zf-1", "c-2", 3)); // promedio de zf-1 = 4.0
        entityManager.persist(comentario("m-3", "zf-2", "c-3", 2)); // promedio de zf-2 = 2.0
        entityManager.flush();

        // Act & Assert: rango de precio
        assertEquals(2, ofertaRepository.buscar(
                new FiltroBusqueda("zz-filtro", null, new BigDecimal("20"), null, null), 0, 10).totalElementos());
        assertEquals(2, ofertaRepository.buscar(
                new FiltroBusqueda("zz-filtro", null, null, new BigDecimal("30"), null), 0, 10).totalElementos());

        // Act & Assert: calificación mínima 4 -> solo zf-1 (la oferta sin comentarios cuenta como 0)
        Pagina<Oferta> altas = ofertaRepository.buscar(new FiltroBusqueda("zz-filtro", null, null, null, 4.0), 0, 10);
        assertEquals(1, altas.totalElementos());
        assertEquals("zf-1", altas.contenido().get(0).getId());

        // Act & Assert: calificación mínima 2 + precio mínimo 20 -> solo zf-2
        Pagina<Oferta> combinada = ofertaRepository.buscar(
                new FiltroBusqueda("zz-filtro", null, new BigDecimal("20"), null, 2.0), 0, 10);
        assertEquals(1, combinada.totalElementos());
        assertEquals("zf-2", combinada.contenido().get(0).getId());
    }

    @Test
    void deberiaOrdenarPorPrecioYPorCalificacion() {
        // Arrange: nombre alfabético zo-1 < zo-2 < zo-3; precios 30, 10, 20; calificaciones 1, sin comentarios, 5
        entityManager.persist(ofertaConPrecio("zo-1", "zz-orden A", "30"));
        entityManager.persist(ofertaConPrecio("zo-2", "zz-orden B", "10"));
        entityManager.persist(ofertaConPrecio("zo-3", "zz-orden C", "20"));
        entityManager.persist(comentario("mo-1", "zo-1", "co-1", 1));
        entityManager.persist(comentario("mo-2", "zo-3", "co-2", 5));
        entityManager.flush();

        // Act & Assert
        assertEquals(List.of("zo-1", "zo-2", "zo-3"), ids(OrdenOfertas.NOMBRE));
        assertEquals(List.of("zo-2", "zo-3", "zo-1"), ids(OrdenOfertas.PRECIO_ASC));
        assertEquals(List.of("zo-1", "zo-3", "zo-2"), ids(OrdenOfertas.PRECIO_DESC));
        assertEquals(List.of("zo-3", "zo-1", "zo-2"), ids(OrdenOfertas.CALIFICACION));
    }

    private List<String> ids(OrdenOfertas orden) {
        Pagina<Oferta> pagina = ofertaRepository.buscar(
                new FiltroBusqueda("zz-orden", null, null, null, null, orden), 0, 10);
        return pagina.contenido().stream().map(Oferta::getId).toList();
    }

    private OfertaEntity ofertaConPrecio(String id, String titulo, String monto) {
        OfertaEntity oferta = crearOferta(id, titulo, EstadoOferta.PUBLICADA);
        oferta.setPrecio(new PrecioEmbeddable(new BigDecimal(monto), "USD"));
        return oferta;
    }

    private ComentarioEntity comentario(String id, String ofertaId, String compraId, int calificacion) {
        ComentarioEntity c = new ComentarioEntity();
        c.setId(id);
        c.setCompradorId("u-2");
        c.setOfertaId(ofertaId);
        c.setCompraId(compraId);
        c.setTexto("Comentario");
        c.setCalificacion(calificacion);
        c.setFecha(java.time.LocalDateTime.now());
        return c;
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