package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaVentas;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Usa el data.sql real: las ofertas o-101 (Zelda) y o-102 (God of War) ya existen.
// @Transactional deshace al final de cada prueba lo que se guardó, así no se ensucian los datos.
@SpringBootTest
@Transactional
class CompraRepositoryJpaTest {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deberiaGuardarYRecuperarUnaCompraConSuPrecioYEstado() {
        // Arrange
        Compra compra = Compra.realizar("c-test-1", "o-101", "u-2",
                new Precio(new BigDecimal("59.99"), "USD"));

        // Act
        compraRepository.guardar(compra);
        // flush + clear obligan a que la lectura vaya de verdad a la base de datos
        // y no se devuelva el objeto que ya estaba en memoria.
        entityManager.flush();
        entityManager.clear();
        Compra recuperada = compraRepository.obtenerPorId("c-test-1").orElseThrow();

        // Assert
        assertEquals("c-test-1", recuperada.getId());
        assertEquals("o-101", recuperada.getOfertaId());
        assertEquals("u-2", recuperada.getCompradorId());
        assertEquals(0, new BigDecimal("59.99").compareTo(recuperada.getPrecioCongelado().valor()));
        assertEquals("USD", recuperada.getPrecioCongelado().moneda());
        assertEquals(EstadoCompra.PENDIENTE, recuperada.getEstado());
    }

    @Test
    void deberiaRankearLasOfertasPorComprasCompletadasIgnorandoLasPendientes() {
        // Arrange: o-102 tiene 2 completadas, o-101 tiene 1 completada y 1 pendiente (esa no cuenta)
        compraRepository.guardar(compraCompletada("c-r1", "o-102"));
        compraRepository.guardar(compraCompletada("c-r2", "o-102"));
        compraRepository.guardar(compraCompletada("c-r3", "o-101"));
        compraRepository.guardar(Compra.realizar("c-r4", "o-101", "u-2",
                new Precio(new BigDecimal("59.99"), "USD")));
        entityManager.flush();

        // Act
        List<OfertaVentas> ranking = ofertaRepository.masVendidas(5);

        // Assert
        assertEquals(2, ranking.size());
        assertEquals("o-102", ranking.get(0).ofertaId());
        assertEquals(2L, ranking.get(0).ventas());
        assertEquals("o-101", ranking.get(1).ofertaId());
        assertEquals(1L, ranking.get(1).ventas());
    }

    private Compra compraCompletada(String id, String ofertaId) {
        Compra compra = Compra.realizar(id, ofertaId, "u-2", new Precio(new BigDecimal("49.99"), "USD"));
        compra.confirmar();
        return compra;
    }
}