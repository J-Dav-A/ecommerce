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

// Notificaciones dentro de la app con seguridad, casos de uso y H2 reales.
// juan@gmail.com (u-2, COMPRADOR) compra o-101; admin@ecommerce.com (u-1, VENDEDOR) es el dueño de la oferta.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificacionesIntegracionTest {

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

    private String comprar() throws Exception {
        String respuesta = mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.id");
    }

    private void confirmar(String compraId) throws Exception {
        mockMvc.perform(put("/api/compras/{id}/confirmar", compraId).header("Authorization", tokenComprador()))
                .andExpect(status().isOk());
    }

    @Test
    void comprarAvisaAlVendedorYConfirmarAvisaAlComprador() throws Exception {
        // Act
        String compraId = comprar();
        confirmar(compraId);

        // Assert: el vendedor ve la venta
        mockMvc.perform(get("/api/notificaciones").header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("VENTA_REALIZADA"))
                .andExpect(jsonPath("$[0].referenciaId").value(compraId))
                .andExpect(jsonPath("$[0].leida").value(false));

        // Assert: el comprador ve su confirmación y no la venta
        mockMvc.perform(get("/api/notificaciones").header("Authorization", tokenComprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("COMPRA_CONFIRMADA"));
    }

    @Test
    void elReembolsoYElComentarioAvisanAlVendedor() throws Exception {
        // Arrange: una compra reembolsada
        String compraId = comprar();
        confirmar(compraId);

        // Act
        mockMvc.perform(put("/api/compras/{id}/reembolso", compraId)
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"motivo\": \"No era lo que esperaba\" }"))
                .andExpect(status().isOk());

        // Assert: más reciente primero; venta + reembolso
        mockMvc.perform(get("/api/notificaciones").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.tipo == 'REEMBOLSO_SOLICITADO')].mensaje")
                        .value(org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("No era lo que esperaba"))));
    }

    @Test
    void unComentarioNuevoAvisaAlVendedor() throws Exception {
        // Arrange
        confirmar(comprar());

        // Act
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\", \"texto\": \"Muy bueno\", \"calificacion\": 5 }"))
                .andExpect(status().isCreated());

        // Assert
        mockMvc.perform(get("/api/notificaciones").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$[?(@.tipo == 'COMENTARIO_NUEVO')]").isNotEmpty());
    }

    @Test
    void sePuedenContarMarcarUnaYMarcarTodas() throws Exception {
        // Arrange: el vendedor recibe 2 avisos (venta y comentario)
        confirmar(comprar());
        mockMvc.perform(post("/api/comentarios")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\", \"texto\": \"Muy bueno\", \"calificacion\": 5 }"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/notificaciones/no-leidas/cantidad").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.cantidad").value(2));

        // Act: marca una
        String lista = mockMvc.perform(get("/api/notificaciones").header("Authorization", tokenVendedor()))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(lista, "$[0].id");
        mockMvc.perform(put("/api/notificaciones/{id}/leida", id).header("Authorization", tokenVendedor()))
                .andExpect(status().isNoContent());

        // Assert
        mockMvc.perform(get("/api/notificaciones/no-leidas/cantidad").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.cantidad").value(1));
        mockMvc.perform(get("/api/notificaciones").param("soloNoLeidas", "true").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.length()").value(1));

        // Act: marca todas
        mockMvc.perform(put("/api/notificaciones/leidas").header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marcadas").value(1));
        mockMvc.perform(get("/api/notificaciones/no-leidas/cantidad").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.cantidad").value(0));
    }

    @Test
    void laNotificacionDeOtroUsuarioDevuelve404() throws Exception {
        // Arrange
        confirmar(comprar());
        String lista = mockMvc.perform(get("/api/notificaciones").header("Authorization", tokenVendedor()))
                .andReturn().getResponse().getContentAsString();
        String idDelVendedor = JsonPath.read(lista, "$[0].id");

        // Act & Assert: el comprador no puede marcarla
        mockMvc.perform(put("/api/notificaciones/{id}/leida", idDelVendedor).header("Authorization", tokenComprador()))
                .andExpect(status().isNotFound());
    }
}