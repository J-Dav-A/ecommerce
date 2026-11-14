package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsultaDeCompraYOfertasDelVendedorTest {

    private final Precio precio = new Precio(new BigDecimal("20"), "USD");

    @Test
    void elCompradorPuedeConsultarSuCompraPeroNoLaDeOtro() {
        // Arrange
        CompraRepository compraRepository = new CompraRepositoryEnMemoria();
        compraRepository.guardar(Compra.realizar("c-1", "o-1", "u-1", precio));
        ConsultarCompra consultarCompra = new ConsultarCompra(compraRepository);

        // Act & Assert
        assertEquals("c-1", consultarCompra.ejecutar("c-1", "u-1").getId());
        assertThrows(RecursoNoEncontradoException.class, () -> consultarCompra.ejecutar("c-1", "u-9"));
        assertThrows(RecursoNoEncontradoException.class, () -> consultarCompra.ejecutar("no-existe", "u-1"));
    }

    @Test
    void elVendedorSoloVeSusOfertasYNoLasEliminadas() {
        // Arrange
        OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
        ofertaRepository.guardar(Oferta.crear("o-1", "v-1", "Juego 1", precio, 5));
        ofertaRepository.guardar(Oferta.crear("o-2", "v-1", "Juego 2", precio, 5));
        ofertaRepository.guardar(Oferta.crear("o-3", "v-2", "Juego de otro", precio, 5));
        Oferta eliminada = Oferta.crear("o-4", "v-1", "Juego eliminado", precio, 5);
        eliminada.eliminar();
        ofertaRepository.guardar(eliminada);
        ListarOfertasDelVendedor listar = new ListarOfertasDelVendedor(ofertaRepository);

        // Act
        List<Oferta> ofertas = listar.ejecutar("v-1");

        // Assert
        assertEquals(2, ofertas.size());
        assertEquals(List.of("o-1", "o-2"), ofertas.stream().map(Oferta::getId).sorted().toList());
    }
}