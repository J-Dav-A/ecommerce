package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Datos conocidos (data.sql): 2 ofertas PUBLICADAS -> "God of War Ragnarok" y "Zelda: Tears of the Kingdom".
// La consulta ordena por título, así que el orden esperado es ese.
@SpringBootTest
@Transactional
class OfertaRepositoryJpaTest {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Test
    void deberiaPaginarLasOfertasPublicadasOrdenadasPorTitulo() {
        // Arrange: tamaño de página 1 para forzar 2 páginas con las 2 ofertas del data.sql

        // Act
        Pagina<Oferta> primeraPagina = ofertaRepository.buscar("", null, 0, 1);
        Pagina<Oferta> segundaPagina = ofertaRepository.buscar("", null, 1, 1);

        // Assert
        assertEquals(2, primeraPagina.totalElementos());
        assertEquals(2, primeraPagina.totalPaginas());
        assertEquals(1, primeraPagina.contenido().size());
        assertEquals("God of War Ragnarok", primeraPagina.contenido().get(0).getTitulo());
        assertEquals("Zelda: Tears of the Kingdom", segundaPagina.contenido().get(0).getTitulo());
    }
}