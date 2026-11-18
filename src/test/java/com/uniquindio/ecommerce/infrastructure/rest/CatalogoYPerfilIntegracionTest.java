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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Datos del data.sql: Zelda o-101 (59.99, NINTENDO) y God of War o-102 (49.99, PLAYSTATION), ambas de u-1 (VENDEDOR);
// juan@gmail.com es u-2 (COMPRADOR).
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogoYPerfilIntegracionTest {

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

    private String comprarYConfirmarO101() throws Exception {
        String respuesta = mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String compraId = JsonPath.read(respuesta, "$.id");
        mockMvc.perform(put("/api/compras/{id}/confirmar", compraId).header("Authorization", tokenComprador()))
                .andExpect(status().isOk());
        return compraId;
    }

    @Test
    void lasTarjetasDelCatalogoTraenImagenPrincipalYPromedioDeCalificacion() throws Exception {
        // Arrange: o-101 con galería y un comentario de 4 estrellas
        mockMvc.perform(put("/api/ofertas/{id}", "o-101").header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"titulo\": \"Zelda: Tears of the Kingdom\", \"precioValor\": 59.99, \"precioMoneda\": \"USD\", "
                                + "\"stock\": 5, \"plataforma\": \"NINTENDO\", \"imagenes\": [\"https://img.test/a.png\", \"https://img.test/b.png\"] }"))
                .andExpect(status().isOk());
        comprarYConfirmarO101();
        mockMvc.perform(post("/api/comentarios").header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\", \"texto\": \"Muy bueno\", \"calificacion\": 4 }"))
                .andExpect(status().isCreated());

        // Act & Assert: el catálogo (orden por nombre: God of War antes que Zelda)
        mockMvc.perform(get("/api/ofertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("o-102"))
                .andExpect(jsonPath("$.content[0].promedioCalificacion").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.content[0].totalComentarios").value(0))
                .andExpect(jsonPath("$.content[1].id").value("o-101"))
                .andExpect(jsonPath("$.content[1].promedioCalificacion").value(4.0))
                .andExpect(jsonPath("$.content[1].totalComentarios").value(1))
                .andExpect(jsonPath("$.content[1].imagenPrincipal").value("https://img.test/a.png"));

        // Act & Assert: el detalle también trae el promedio
        mockMvc.perform(get("/api/ofertas/{id}", "o-101"))
                .andExpect(jsonPath("$.promedioCalificacion").value(4.0))
                .andExpect(jsonPath("$.imagenes.length()").value(2));
    }

    @Test
    void elCatalogoSePuedeOrdenarPorPrecioPopularidadYCalificacion() throws Exception {
        // Arrange: o-101 tiene una venta completada y un comentario de 5 estrellas
        comprarYConfirmarO101();
        mockMvc.perform(post("/api/comentarios").header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\", \"texto\": \"Genial\", \"calificacion\": 5 }"))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/ofertas").param("orden", "PRECIO_ASC"))
                .andExpect(jsonPath("$.content[0].id").value("o-102"));
        mockMvc.perform(get("/api/ofertas").param("orden", "PRECIO_DESC"))
                .andExpect(jsonPath("$.content[0].id").value("o-101"));
        mockMvc.perform(get("/api/ofertas").param("orden", "POPULARIDAD"))
                .andExpect(jsonPath("$.content[0].id").value("o-101"));
        mockMvc.perform(get("/api/ofertas").param("orden", "CALIFICACION"))
                .andExpect(jsonPath("$.content[0].id").value("o-101"));
    }

    @Test
    void unaOfertaEliminadaDejaDeVerseEnElDetalleYEnElCatalogo() throws Exception {
        // Act
        mockMvc.perform(delete("/api/ofertas/{id}", "o-102").header("Authorization", tokenVendedor()))
                .andExpect(status().isOk());

        // Assert
        mockMvc.perform(get("/api/ofertas/{id}", "o-102")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/ofertas"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void elReembolsoTambienFuncionaPorPutYElHistorialSeFiltra() throws Exception {
        // Arrange
        String compraId = comprarYConfirmarO101();

        // Act & Assert: PUT /compras/{id}/reembolso (como en el mapeo de la profesora)
        mockMvc.perform(put("/api/compras/{id}/reembolso", compraId).header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"motivo\": \"Ya no lo quiero\" }"))
                .andExpect(status().isOk());

        // Assert: filtros del historial
        mockMvc.perform(get("/api/compras").param("estado", "REEMBOLSADA").header("Authorization", tokenComprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/compras").param("estado", "COMPLETADA").header("Authorization", tokenComprador()))
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/compras").param("desde", "2020-01-01").param("hasta", "2020-12-31")
                        .header("Authorization", tokenComprador()))
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/compras").param("desde", "2026-12-31").param("hasta", "2026-01-01")
                        .header("Authorization", tokenComprador()))
                .andExpect(status().isUnprocessableEntity());

        // Un vendedor no puede pedir reembolsos
        mockMvc.perform(put("/api/compras/{id}/reembolso", compraId).header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON).content("{ \"motivo\": \"x\" }"))
                .andExpect(status().isForbidden());
    }

    @Test
    void elVendedorCompletaSuPerfilYElCompradorNoPuedeUsarCamposDeVendedor() throws Exception {
        // Act & Assert: vendedor con foto, descripción y datos de pago
        mockMvc.perform(put("/api/usuarios/me").header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Admin Gamer\", \"fotoPerfil\": \"https://img.test/admin.png\", "
                                + "\"descripcion\": \"Claves originales\", \"datosPago\": \"Nequi 3001234567\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoPerfil").value("https://img.test/admin.png"))
                .andExpect(jsonPath("$.descripcion").value("Claves originales"));
        mockMvc.perform(get("/api/usuarios/me").header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.datosPago").value("Nequi 3001234567"));

        // Comprador con descripción -> 422; con foto válida -> 200; con foto inválida -> 422
        mockMvc.perform(put("/api/usuarios/me").header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Juan\", \"descripcion\": \"Algo\" }"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(put("/api/usuarios/me").header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Juan\", \"fotoPerfil\": \"https://img.test/juan.png\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fotoPerfil").value("https://img.test/juan.png"));
        mockMvc.perform(put("/api/usuarios/me").header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Juan\", \"fotoPerfil\": \"ftp://malo\" }"))
                .andExpect(status().isUnprocessableEntity());
    }
}