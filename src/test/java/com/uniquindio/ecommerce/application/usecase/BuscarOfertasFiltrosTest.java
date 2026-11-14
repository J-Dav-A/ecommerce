package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.FiltroBusqueda;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuscarOfertasFiltrosTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final BuscarOfertas buscarOfertas = new BuscarOfertas(ofertaRepository);

    private void publicar(String id, String titulo, String precio) {
        Oferta oferta = Oferta.crear(id, "v-1", titulo, new Precio(new BigDecimal(precio), "USD"), 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        ofertaRepository.guardar(oferta);
    }

    @BeforeEach
    void preparar() {
        publicar("o-1", "Barato", "10");
        publicar("o-2", "Medio", "30");
        publicar("o-3", "Caro", "60");
    }

    private List<String> ids(Pagina<Oferta> pagina) {
        return pagina.contenido().stream().map(Oferta::getId).sorted().toList();
    }

    @Test
    void filtraPorRangoDePrecioInclusivo() {
        // Act & Assert
        assertEquals(List.of("o-2", "o-3"),
                ids(buscarOfertas.buscar(new FiltroBusqueda(null, null, new BigDecimal("30"), null, null), 0, 10)));
        assertEquals(List.of("o-1", "o-2"),
                ids(buscarOfertas.buscar(new FiltroBusqueda(null, null, null, new BigDecimal("30"), null), 0, 10)));
        assertEquals(List.of("o-2"),
                ids(buscarOfertas.buscar(new FiltroBusqueda(null, null, new BigDecimal("20"), new BigDecimal("40"), null), 0, 10)));
    }

    @Test
    void sinFiltrosDevuelveTodasLasPublicadas() {
        assertEquals(List.of("o-1", "o-2", "o-3"),
                ids(buscarOfertas.buscar(new FiltroBusqueda(null, null, null, null, null), 0, 10)));
    }

    @Test
    void rechazaFiltrosInvalidos() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> buscarOfertas.buscar(new FiltroBusqueda(null, null, new BigDecimal("-1"), null, null), 0, 10));
        assertThrows(ReglaDominioException.class,
                () -> buscarOfertas.buscar(new FiltroBusqueda(null, null, new BigDecimal("50"), new BigDecimal("10"), null), 0, 10));
        assertThrows(ReglaDominioException.class,
                () -> buscarOfertas.buscar(new FiltroBusqueda(null, null, null, null, 0.5), 0, 10));
        assertThrows(ReglaDominioException.class,
                () -> buscarOfertas.buscar(new FiltroBusqueda(null, null, null, null, 5.5), 0, 10));
    }
}