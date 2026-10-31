package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba de integración: NO hay ningún @MockitoBean. La petición pasa por la seguridad real (JWT),
// el controller, el caso de uso RegistrarCompra, los repositorios JPA y la base H2 con el data.sql.
// @Transactional deshace lo guardado al terminar cada prueba.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ComprarOfertaIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Test
    void deberiaComprarUnaOfertaDejarlaPendienteYDescontarElStock() throws Exception {
        // Arrange
        String token = jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");
        int stockAntes = ofertaRepository.obtenerPorId("o-101").orElseThrow().getStock();
        String cuerpo = """
                { "ofertaId": "o-101", "compradorId": "u-2" }
                """;

        // Act
        ResultActions respuesta = mockMvc.perform(post("/api/compras")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));

        // Assert: la respuesta HTTP y, además, lo que quedó realmente guardado en la base
        respuesta.andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.ofertaId").value("o-101"));
        assertEquals(1, compraRepository.obtenerPorCompradorId("u-2").size());
        assertEquals(stockAntes - 1, ofertaRepository.obtenerPorId("o-101").orElseThrow().getStock());
    }

    @Test
    void noDeberiaGuardarNingunaCompraSiLaOfertaNoExiste() throws Exception {
        // Arrange
        String token = jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");
        String cuerpo = """
                { "ofertaId": "no-existe", "compradorId": "u-2" }
                """;

        // Act
        ResultActions respuesta = mockMvc.perform(post("/api/compras")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));

        // Assert
        respuesta.andExpect(status().isBadRequest());
        assertTrue(compraRepository.obtenerPorCompradorId("u-2").isEmpty());
    }
}