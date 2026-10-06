package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarCompraTest {

    @Test
    void alRegistrarUnaCompraSeAsignaUnaClaveDigitalDisponible() {
        // Arrange
        OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
        CompraRepository compraRepository = new CompraRepositoryEnMemoria();
        ClaveDigitalRepository claveDigitalRepository = new ClaveDigitalRepositoryEnMemoria();

        UUID vendedorId = UUID.randomUUID();
        UUID compradorId = UUID.randomUUID();

        Oferta oferta = Oferta.crear(UUID.randomUUID(), vendedorId, "Juego de prueba",
                new Precio(new BigDecimal("59.99")), 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        ofertaRepository.guardar(oferta);

        ClaveDigital clave = ClaveDigital.crear(UUID.randomUUID(), oferta.getId(),
                "XXXX-YYYY", new LicenciaDigital(1, false));
        claveDigitalRepository.guardar(clave);

        RegistrarCompra registrarCompra = new RegistrarCompra(
                ofertaRepository, compraRepository, claveDigitalRepository);

        // Act
        Compra compra = registrarCompra.ejecutar(oferta.getId(), compradorId);

        // Assert
        ClaveDigital claveAsignada = claveDigitalRepository.obtenerPorId(clave.getId()).orElseThrow();
        assertEquals(EstadoClaveDigital.ASIGNADA, claveAsignada.getEstado());
        assertEquals(compradorId, claveAsignada.getCompradorId());
    }

    @Test
    void noDebeRegistrarLaCompraSiNoHayClavesDigitalesDisponibles() {
        // Arrange
        OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
        CompraRepository compraRepository = new CompraRepositoryEnMemoria();
        ClaveDigitalRepository claveDigitalRepository = new ClaveDigitalRepositoryEnMemoria();

        UUID vendedorId = UUID.randomUUID();
        UUID compradorId = UUID.randomUUID();

        Oferta oferta = Oferta.crear(UUID.randomUUID(), vendedorId, "Juego sin claves",
                new Precio(new BigDecimal("39.99")), 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        ofertaRepository.guardar(oferta);

        // No se guarda ninguna ClaveDigital para esta oferta.

        RegistrarCompra registrarCompra = new RegistrarCompra(
                ofertaRepository, compraRepository, claveDigitalRepository);

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> registrarCompra.ejecutar(oferta.getId(), compradorId));
    }
}
