package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Comprueba que el usuario que actúa sale del token y NO del cuerpo de la petición.
// juan@gmail.com (token) es u-2; admin@ecommerce.com es u-1 (datos del data.sql).
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IdentidadDelTokenIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CompraRepository compraRepository;

    @Test
    void laCompraQuedaAnombreDelDuenoDelTokenAunqueElCuerpoIntenteOtroComprador() throws Exception {
        // Arrange: el cuerpo trae un compradorId ajeno (u-1) que debe ser ignorado
        String token = jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");
        String cuerpo = """
                { "ofertaId": "o-101", "compradorId": "u-1" }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.compradorId").value("u-2"));
        assertEquals(1, compraRepository.obtenerPorCompradorId("u-2").size());
        assertEquals(0, compraRepository.obtenerPorCompradorId("u-1").size());
    }

    @Test
    void noDebePermitirReembolsarLaCompraDeOtroComprador() throws Exception {
        // Arrange: compra completada que pertenece a u-1; quien intenta reembolsar es juan (u-2)
        compraRepository.guardar(Compra.reconstruir("c-ajena", "o-101", "u-1",
                new Precio(new BigDecimal("59.99"), "USD"), LocalDateTime.now(), EstadoCompra.COMPLETADA));
        String token = jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");

        // Act & Assert
        mockMvc.perform(post("/api/compras/{id}/reembolso", "c-ajena")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"motivo\": \"Intento reembolsar lo de otro\" }"))
                .andExpect(status().isNotFound());
        assertEquals(EstadoCompra.COMPLETADA, compraRepository.obtenerPorId("c-ajena").orElseThrow().getEstado());
    }

    @Test
    void elHistorialSoloMuestraLasComprasDelUsuarioDelToken() throws Exception {
        // Arrange: una compra de u-1 y una de u-2 (juan)
        Precio precio = new Precio(new BigDecimal("59.99"), "USD");
        compraRepository.guardar(Compra.realizar("c-de-u1", "o-101", "u-1", precio));
        compraRepository.guardar(Compra.realizar("c-de-u2", "o-101", "u-2", precio));
        String token = jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");

        // Act & Assert
        mockMvc.perform(get("/api/compras")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("c-de-u2"));
    }
}