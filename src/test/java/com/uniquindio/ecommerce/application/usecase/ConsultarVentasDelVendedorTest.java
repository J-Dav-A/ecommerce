package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.usecase.ConsultarVentasDelVendedor.Venta;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultarVentasDelVendedorTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ConsultarVentasDelVendedor consultarVentas =
            new ConsultarVentasDelVendedor(ofertaRepository, compraRepository);

    private final Precio precio = new Precio(new BigDecimal("30"), "USD");
    private final LocalDateTime ahora = LocalDateTime.now();

    @BeforeEach
    void preparar() {
        ofertaRepository.guardar(Oferta.crear("o-1", "v-1", "Juego 1", precio, 5));
        Oferta eliminada = Oferta.crear("o-2", "v-1", "Juego eliminado", precio, 5);
        eliminada.eliminar();
        ofertaRepository.guardar(eliminada);
        ofertaRepository.guardar(Oferta.crear("o-3", "v-2", "De otro vendedor", precio, 5));

        compraRepository.guardar(Compra.reconstruir("c-1", "o-1", "u-1", precio, ahora.minusDays(10), EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-2", "o-1", "u-2", precio, ahora.minusHours(1), EstadoCompra.REEMBOLSADA));
        compraRepository.guardar(Compra.reconstruir("c-3", "o-2", "u-3", precio, ahora.minusDays(5), EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-4", "o-3", "u-4", precio, ahora, EstadoCompra.COMPLETADA));
    }

    private List<String> ids(List<Venta> ventas) {
        return ventas.stream().map(v -> v.compra().getId()).toList();
    }

    @Test
    void sinFiltrosDevuelveTodasSusVentasConElMasRecientePrimeroIncluyendoOfertasEliminadas() {
        // Act
        List<Venta> ventas = consultarVentas.ejecutar("v-1", null, null, null);

        // Assert: no aparece c-4 (es de otro vendedor)
        assertEquals(List.of("c-2", "c-3", "c-1"), ids(ventas));
    }

    @Test
    void filtraPorEstado() {
        // Act & Assert
        assertEquals(List.of("c-3", "c-1"), ids(consultarVentas.ejecutar("v-1", null, null, EstadoCompra.COMPLETADA)));
        assertEquals(List.of("c-2"), ids(consultarVentas.ejecutar("v-1", null, null, EstadoCompra.REEMBOLSADA)));
        assertEquals(List.of(), ids(consultarVentas.ejecutar("v-1", null, null, EstadoCompra.PENDIENTE)));
    }

    @Test
    void filtraPorRangoDeFechasInclusivo() {
        // Act: desde hace 6 días hasta hoy
        List<Venta> ventas = consultarVentas.ejecutar("v-1", LocalDate.now().minusDays(6), LocalDate.now(), null);

        // Assert: queda fuera la compra de hace 10 días
        assertEquals(List.of("c-2", "c-3"), ids(ventas));
    }

    @Test
    void combinaFechasYEstado() {
        // Act
        List<Venta> ventas = consultarVentas.ejecutar("v-1", LocalDate.now().minusDays(6), LocalDate.now(), EstadoCompra.COMPLETADA);

        // Assert
        assertEquals(List.of("c-3"), ids(ventas));
    }

    @Test
    void rechazaUnRangoConLaFechaInicialPosteriorALaFinal() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> consultarVentas.ejecutar("v-1", LocalDate.now(), LocalDate.now().minusDays(1), null));
    }
}