package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.repository.OfertaVentas;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.CompraEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class OfertaJpaDataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OfertaJpaDataRepository ofertaJpaDataRepository;

    @Test
    void masVendidasDevuelveLasOfertasOrdenadasPorCantidadDeVentas() {
        OfertaEntity oferta1 = crearOferta("o-1", "Zelda");
        OfertaEntity oferta2 = crearOferta("o-2", "God of War");
        entityManager.persist(oferta1);
        entityManager.persist(oferta2);

        // o-1 tiene 2 compras completadas, o-2 tiene 1
        entityManager.persist(crearCompra("o-1"));
        entityManager.persist(crearCompra("o-1"));
        entityManager.persist(crearCompra("o-2"));
        entityManager.flush();

        List<OfertaVentas> resultado = ofertaJpaDataRepository.masVendidas(
                EstadoCompra.COMPLETADA, PageRequest.of(0, 5));

        assertEquals(2, resultado.size());
        assertEquals("o-1", resultado.get(0).ofertaId());
        assertEquals(2L, resultado.get(0).ventas());
        assertEquals("o-2", resultado.get(1).ofertaId());
        assertEquals(1L, resultado.get(1).ventas());
    }

    private OfertaEntity crearOferta(String id, String titulo) {
        OfertaEntity oferta = new OfertaEntity();
        oferta.setId(id);
        oferta.setVendedorId("u-1");
        oferta.setTitulo(titulo);
        oferta.setPrecio(new PrecioEmbeddable(new BigDecimal("59.99"), "USD"));
        oferta.setStock(5);
        oferta.setPlataforma(Plataforma.PC);
        oferta.setEstado(EstadoOferta.PUBLICADA);
        return oferta;
    }

    private CompraEntity crearCompra(String ofertaId) {
        CompraEntity compra = new CompraEntity();
        compra.setId(UUID.randomUUID().toString());
        compra.setCompradorId("u-2");
        compra.setOfertaId(ofertaId);
        compra.setPrecio(new PrecioEmbeddable(new BigDecimal("59.99"), "USD"));
        compra.setEstado(EstadoCompra.COMPLETADA);
        compra.setFechaCompra(LocalDateTime.now());
        return compra;
    }
}