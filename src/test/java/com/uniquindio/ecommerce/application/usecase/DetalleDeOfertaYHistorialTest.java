package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DetalleDeOfertaYHistorialTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final BuscarOfertas buscarOfertas = new BuscarOfertas(ofertaRepository);
    private final ConsultarHistorialDeCompras historial = new ConsultarHistorialDeCompras(compraRepository);

    private final Precio precio = new Precio(new BigDecimal("30"), "USD");
    private final LocalDateTime ahora = LocalDateTime.now();

    @Test
    void elDetalleSoloMuestraOfertasPublicadas() {
        // Arrange
        Oferta publicada = Oferta.crear("o-1", "v-1", "Publicada", precio, 5);
        publicada.definirPlataforma(Plataforma.PC);
        publicada.publicar();
        ofertaRepository.guardar(publicada);
        ofertaRepository.guardar(Oferta.crear("o-2", "v-1", "Borrador", precio, 5));
        Oferta eliminada = Oferta.crear("o-3", "v-1", "Eliminada", precio, 5);
        eliminada.eliminar();
        ofertaRepository.guardar(eliminada);

        // Act & Assert
        assertEquals("o-1", buscarOfertas.obtener("o-1").getId());
        assertThrows(RecursoNoEncontradoException.class, () -> buscarOfertas.obtener("o-2"));
        assertThrows(RecursoNoEncontradoException.class, () -> buscarOfertas.obtener("o-3"));
    }

    private void guardarCompras() {
        compraRepository.guardar(Compra.reconstruir("c-1", "o-1", "u-1", precio, ahora.minusDays(10), EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-2", "o-2", "u-1", precio, ahora.minusDays(2), EstadoCompra.REEMBOLSADA));
        compraRepository.guardar(Compra.reconstruir("c-3", "o-3", "u-1", precio, ahora.minusHours(1), EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-4", "o-4", "u-2", precio, ahora, EstadoCompra.COMPLETADA));
    }

    private List<String> ids(List<Compra> compras) {
        return compras.stream().map(Compra::getId).toList();
    }

    @Test
    void elHistorialDelCompradorVieneConElMasRecientePrimeroYSoloDeEseComprador() {
        // Arrange
        guardarCompras();

        // Act & Assert
        assertEquals(List.of("c-3", "c-2", "c-1"), ids(historial.ejecutar("u-1")));
    }

    @Test
    void elHistorialSeFiltraPorEstadoYPorFechas() {
        // Arrange
        guardarCompras();

        // Act & Assert
        assertEquals(List.of("c-2"), ids(historial.ejecutar("u-1", null, null, EstadoCompra.REEMBOLSADA)));
        assertEquals(List.of("c-3", "c-1"), ids(historial.ejecutar("u-1", null, null, EstadoCompra.COMPLETADA)));
        assertEquals(List.of("c-3", "c-2"), ids(historial.ejecutar("u-1", LocalDate.now().minusDays(5), LocalDate.now(), null)));
        assertEquals(List.of("c-3"), ids(historial.ejecutar("u-1", LocalDate.now().minusDays(5), LocalDate.now(), EstadoCompra.COMPLETADA)));
    }

    @Test
    void elHistorialRechazaUnRangoInvertido() {
        assertThrows(ReglaDominioException.class,
                () -> historial.ejecutar("u-1", LocalDate.now(), LocalDate.now().minusDays(1), null));
    }
}