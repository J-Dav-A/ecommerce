
package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.ClaveDigitalRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarCompraOfertaInexistenteTest {

    @Test
    void noDebeRegistrarLaCompraCuandoLaOfertaNoExiste() {
        // Arrange
        OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
        CompraRepository compraRepository = new CompraRepositoryEnMemoria();
        ClaveDigitalRepository claveDigitalRepository = new ClaveDigitalRepositoryEnMemoria();

        String ofertaIdInexistente = UUID.randomUUID().toString();
        String compradorId = UUID.randomUUID().toString();

        RegistrarCompra registrarCompra = new RegistrarCompra(
                ofertaRepository,
                compraRepository,
                claveDigitalRepository
        );

        // Act
        RecursoNoEncontradoException excepcion = assertThrows(
                RecursoNoEncontradoException.class,
                () -> registrarCompra.ejecutar(ofertaIdInexistente, compradorId)
        );

        // Assert
        assertEquals("La oferta no existe", excepcion.getMessage());
    }
}