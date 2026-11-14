package com.uniquindio.ecommerce.infrastructure.rest;

import com.jayway.jsonpath.JsonPath;
import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Flujo de comentarios con la seguridad, los casos de uso y la base H2 reales (sin mocks).
// juan@gmail.com (u-2, COMPRADOR) compra o-101; admin@ecommerce.com (u-1, VENDEDOR) es su dueño.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ComentariosIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private String tokenComprador() {
        return "Bearer " + jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");
    }

    private String tokenVendedor() {
        return "Bearer " + jwtUtils.generarToken("admin@ecommerce.com", "VENDEDOR");
    }

    private void comprarYConfirmarO101() throws Exception {
        String respuesta = mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String compraId = JsonPath.read(respuesta, "$.id");
        mockMvc.perform(put("/api/compras/{id}/confirmar", compraId)
                        .header("Authorization", tokenComprador()))
                .andExpect(status().isOk());
    }

    private String comentarioJson(int calificacion) {
        return "{ \"ofertaId\": \"o-101\", \"texto\": \"Excelente juego\", \"calificacion\": " + calificacion + " }";
    }

    @Test
    void elCompradorComentaUnaVezLaListaEsPublicaYMuestraElPromedio() throws Exception {
        // Arrange
        comprarYConfirmarO101();

        // Act & Assert: comenta con calificación
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson(4)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calificacion").value(4))
                .andExpect(jsonPath("$.compradorId").value("u-2"));

        // Act & Assert: un segundo comentario sobre la misma compra se rechaza
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson(5)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("Ya comentaste esta compra"));

        // Act & Assert: la lista es pública (sin token) e incluye el promedio
        mockMvc.perform(get("/api/comentarios").param("ofertaId", "o-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.promedioCalificacion").value(4.0))
                .andExpect(jsonPath("$.comentarios[0].texto").value("Excelente juego"));
    }

    @Test
    void noDebePermitirComentarSinCompraNiConCalificacionInvalida() throws Exception {
        // Act & Assert: sin compra completada
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson(5)))
                .andExpect(status().isUnprocessableEntity());

        // Act & Assert: calificación fuera de 1 a 5 (validación de entrada)
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson(7)))
                .andExpect(status().isBadRequest());

        // Act & Assert: un vendedor no tiene el rol de comprador
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson(5)))
                .andExpect(status().isForbidden());
    }

    @Test
    void elVendedorVeElComentarioPendienteLoRespondeYAparecenSusMetricas() throws Exception {
        // Arrange
        comprarYConfirmarO101();
        String respuesta = mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson(5)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String comentarioId = JsonPath.read(respuesta, "$.id");

        // Act & Assert: el comentario aparece como pendiente en la bandeja del vendedor
        mockMvc.perform(get("/api/vendedor/comentarios").param("sinResponder", "true")
                        .header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Act & Assert: un comprador no puede responder
        mockMvc.perform(put("/api/comentarios/{id}/respuesta", comentarioId)
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"respuesta\": \"Intento responder\" }"))
                .andExpect(status().isForbidden());

        // Act & Assert: el vendedor dueño responde
        mockMvc.perform(put("/api/comentarios/{id}/respuesta", comentarioId)
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"respuesta\": \"Gracias por tu compra\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value("Gracias por tu compra"));

        // Act & Assert: ya no aparece como pendiente
        mockMvc.perform(get("/api/vendedor/comentarios").param("sinResponder", "true")
                        .header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.length()").value(0));

        // Act & Assert: métricas de o-101 (1 venta completada y 1 calificación de 5)
        mockMvc.perform(get("/api/vendedor/metricas")
                        .header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.ofertaId=='o-101')].ventas").value(1))
                .andExpect(jsonPath("$[?(@.ofertaId=='o-101')].promedioCalificacion").value(5.0));

        // Act & Assert: con un rango de fechas en el pasado no hay ventas
        mockMvc.perform(get("/api/vendedor/metricas")
                        .param("desde", "2020-01-01").param("hasta", "2020-12-31")
                        .header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.ofertaId=='o-101')].ventas").value(0));

        // Act & Assert: un comprador no puede ver las métricas del vendedor
        mockMvc.perform(get("/api/vendedor/metricas")
                        .header("Authorization", tokenComprador()))
                .andExpect(status().isForbidden());
    }
}