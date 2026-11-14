package com.uniquindio.ecommerce.infrastructure.rest;

import com.jayway.jsonpath.JsonPath;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Flujo completo con la seguridad, los casos de uso y la base H2 reales (sin mocks).
// Datos del data.sql: o-101 (vendedor u-1) tiene 2 claves DISPONIBLES; juan@gmail.com es u-2 (COMPRADOR).
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlujoDeClavesIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CompraRepository compraRepository;

    private String tokenComprador() {
        return "Bearer " + jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");
    }

    private String tokenVendedor() {
        return "Bearer " + jwtUtils.generarToken("admin@ecommerce.com", "VENDEDOR");
    }

    private String comprarYConfirmar(String ofertaId) throws Exception {
        String respuesta = mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"" + ofertaId + "\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String compraId = JsonPath.read(respuesta, "$.id");

        mockMvc.perform(put("/api/compras/{id}/confirmar", compraId)
                        .header("Authorization", tokenComprador()))
                .andExpect(status().isOk());
        return compraId;
    }

    @Test
    void elCompradorRecibeSuClaveYYaNoPuedeReembolsar() throws Exception {
        // Arrange
        String compraId = comprarYConfirmar("o-101");

        // Act & Assert: recibir la clave la canjea
        mockMvc.perform(post("/api/compras/{id}/clave", compraId)
                        .header("Authorization", tokenComprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANJEADA"))
                .andExpect(jsonPath("$.codigo").value(org.hamcrest.Matchers.startsWith("ZELDA-2026-KEY")));

        // Act & Assert: con la clave canjeada el reembolso se rechaza con 422
        mockMvc.perform(post("/api/compras/{id}/reembolso", compraId)
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"motivo\": \"Ya no lo quiero\" }"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("No se reembolsan claves ya canjeadas"));
        assertEquals(EstadoCompra.COMPLETADA, compraRepository.obtenerPorId(compraId).orElseThrow().getEstado());
    }

    @Test
    void sinHaberRecibidoLaClaveElReembolsoFuncionaYSePuedeVolverAComprar() throws Exception {
        // Arrange
        String compraId = comprarYConfirmar("o-101");

        // Act: reembolso dentro del plazo y sin canjear
        mockMvc.perform(post("/api/compras/{id}/reembolso", compraId)
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"motivo\": \"No era lo que esperaba\" }"))
                .andExpect(status().isOk());

        // Assert: la compra queda reembolsada y el comprador puede volver a comprar (usa la otra clave)
        assertEquals(EstadoCompra.REEMBOLSADA, compraRepository.obtenerPorId(compraId).orElseThrow().getEstado());
        mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\" }"))
                .andExpect(status().isCreated());
    }

    @Test
    void noDebePermitirComprarDosVecesLaMismaOferta() throws Exception {
        // Arrange
        comprarYConfirmar("o-101");

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\" }"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("Ya compraste esta oferta"));
    }

    @Test
    void elVendedorCargaUnaClaveYUnCompradorNoPuede() throws Exception {
        // Act & Assert: el vendedor dueño de o-101 puede cargar claves
        mockMvc.perform(post("/api/ofertas/{id}/claves", "o-101")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"codigo\": \"ZELDA-NUEVA-KEY\", \"maxActivaciones\": 1, \"transferible\": false }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
                .andExpect(jsonPath("$.ofertaId").value("o-101"));

        // Act & Assert: un comprador no tiene el rol para hacerlo
        mockMvc.perform(post("/api/ofertas/{id}/claves", "o-101")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"codigo\": \"OTRA-KEY\", \"maxActivaciones\": 1, \"transferible\": false }"))
                .andExpect(status().isForbidden());
    }

    @Test
    void alPublicarUnaOfertaElVendedorSaleDelTokenYApareceEnMisOfertas() throws Exception {
        // Arrange: el cuerpo ya no trae vendedorId
        String cuerpo = """
                { "titulo": "Hades II", "precioValor": 29.99, "precioMoneda": "USD", "stock": 10, "plataforma": "PC" }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/ofertas")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vendedorId").value("u-1"));

        mockMvc.perform(get("/api/vendedor/ofertas")
                        .header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // Un comprador no puede ver el panel del vendedor
        mockMvc.perform(get("/api/vendedor/ofertas")
                        .header("Authorization", tokenComprador()))
                .andExpect(status().isForbidden());
    }

    @Test
    void noDebeEliminarUnaOfertaConUnaCompraRecienteSujetaAReembolso() throws Exception {
        // Arrange: juan compra o-101 hace un momento
        comprarYConfirmar("o-101");

        // Act & Assert: el vendedor no puede eliminarla todavía (422)
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/ofertas/{id}", "o-101")
                        .header("Authorization", tokenVendedor()))
                .andExpect(status().isUnprocessableEntity());
    }
}