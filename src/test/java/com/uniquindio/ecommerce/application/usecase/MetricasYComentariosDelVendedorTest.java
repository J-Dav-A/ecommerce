package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.usecase.ConsultarMetricasDelVendedor.MetricaOferta;
import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ComentarioRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MetricasYComentariosDelVendedorTest {

    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final ComentarioRepository comentarioRepository = new ComentarioRepositoryEnMemoria();
    private final ConsultarMetricasDelVendedor consultarMetricas =
            new ConsultarMetricasDelVendedor(ofertaRepository, compraRepository, comentarioRepository);
    private final ListarComentariosDelVendedor listarComentarios =
            new ListarComentariosDelVendedor(ofertaRepository, comentarioRepository);

    private final Precio precio = new Precio(new BigDecimal("30"), "USD");
    private final LocalDateTime ahora = LocalDateTime.now();

    @BeforeEach
    void preparar() {
        ofertaRepository.guardar(Oferta.crear("o-1", "v-1", "Juego 1", precio, 5));
        ofertaRepository.guardar(Oferta.crear("o-2", "v-1", "Juego 2", precio, 5));
        ofertaRepository.guardar(Oferta.crear("o-3", "v-2", "Juego de otro vendedor", precio, 5));

        // o-1: dos ventas completadas (una de hace 10 días y otra de hoy), una reembolsada y una pendiente
        compraRepository.guardar(Compra.reconstruir("c-1", "o-1", "u-1", precio, ahora.minusDays(10), EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-2", "o-1", "u-2", precio, ahora, EstadoCompra.COMPLETADA));
        compraRepository.guardar(Compra.reconstruir("c-3", "o-1", "u-3", precio, ahora, EstadoCompra.REEMBOLSADA));
        compraRepository.guardar(Compra.reconstruir("c-4", "o-1", "u-4", precio, ahora, EstadoCompra.PENDIENTE));

        // o-1: un comentario de 5 estrellas de hoy (sin responder) y uno de 3 estrellas de hace 10 días (respondido)
        comentarioRepository.guardar(Comentario.reconstruir("m-1", "u-2", "o-1", "c-2", "Muy bueno", 5, ahora, null, null));
        comentarioRepository.guardar(Comentario.reconstruir("m-2", "u-1", "o-1", "c-1", "Regular", 3,
                ahora.minusDays(10), "Gracias", ahora.minusDays(9)));
        // Comentario de una oferta de otro vendedor
        comentarioRepository.guardar(Comentario.reconstruir("m-3", "u-5", "o-3", "c-9", "Ajeno", 1, ahora, null, null));
    }

    private MetricaOferta metricaDe(List<MetricaOferta> metricas, String ofertaId) {
        return metricas.stream().filter(m -> m.oferta().getId().equals(ofertaId)).findFirst().orElseThrow();
    }

    @Test
    void sinRangoCuentaTodoElHistorialDeCadaOfertaDelVendedor() {
        // Act
        List<MetricaOferta> metricas = consultarMetricas.ejecutar("v-1", null, null);

        // Assert: solo las ofertas de v-1; las reembolsadas y pendientes no cuentan como venta
        assertEquals(2, metricas.size());
        MetricaOferta o1 = metricaDe(metricas, "o-1");
        assertEquals(2, o1.ventas());
        assertEquals(4.0, o1.promedioCalificacion());
        assertEquals(2, o1.totalComentarios());
        MetricaOferta o2 = metricaDe(metricas, "o-2");
        assertEquals(0, o2.ventas());
        assertNull(o2.promedioCalificacion());
    }

    @Test
    void conRangoDeFechasSoloCuentaLoQueCaeDentro() {
        // Act: los últimos 2 días (incluye hoy)
        List<MetricaOferta> metricas = consultarMetricas.ejecutar("v-1", LocalDate.now().minusDays(2), LocalDate.now());

        // Assert: solo la venta y el comentario de hoy
        MetricaOferta o1 = metricaDe(metricas, "o-1");
        assertEquals(1, o1.ventas());
        assertEquals(5.0, o1.promedioCalificacion());
        assertEquals(1, o1.totalComentarios());
    }

    @Test
    void noDebeAceptarUnRangoConLaFechaInicialPosteriorALaFinal() {
        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> consultarMetricas.ejecutar("v-1", LocalDate.now(), LocalDate.now().minusDays(1)));

        // Assert
        assertEquals("La fecha inicial no puede ser posterior a la final", excepcion.getMessage());
    }

    @Test
    void elVendedorVeLosComentariosDeSusOfertasYPuedeFiltrarLosSinResponder() {
        // Act
        List<Comentario> todos = listarComentarios.ejecutar("v-1", false);
        List<Comentario> pendientes = listarComentarios.ejecutar("v-1", true);

        // Assert: el más reciente primero, y sin los comentarios de ofertas de otros vendedores
        assertEquals(List.of("m-1", "m-2"), todos.stream().map(Comentario::getId).toList());
        assertEquals(List.of("m-1"), pendientes.stream().map(Comentario::getId).toList());
    }
}