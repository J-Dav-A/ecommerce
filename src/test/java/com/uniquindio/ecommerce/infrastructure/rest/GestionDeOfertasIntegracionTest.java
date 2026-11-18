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

// Seguridad, casos de uso y H2 reales. admin@ecommerce.com (u-1, VENDEDOR) es dueño de o-101 y o-102;
// juan@gmail.com (u-2, COMPRADOR) compra.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GestionDeOfertasIntegracionTest {

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

    private String cuerpo(String titulo, String imagenes) {
        return "{ \"titulo\": \"" + titulo + "\", \"precioValor\": 49.99, \"precioMoneda\": \"USD\", "
                + "\"stock\": 7, \"plataforma\": \"PC\"" + (imagenes == null ? "" : ", \"imagenes\": " + imagenes) + " }";
    }

    @Test
    void elVendedorEditaSuOfertaConImagenesYQuedanVisiblesEnElCatalogo() throws Exception {
        // Act & Assert: edición con galería
        mockMvc.perform(put("/api/ofertas/{id}", "o-101")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("Zelda Deluxe", "[\"https://img.test/a.png\", \"https://img.test/b.png\"]")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Zelda Deluxe"))
                .andExpect(jsonPath("$.stock").value(7))
                .andExpect(jsonPath("$.imagenes.length()").value(2));

        // Act & Assert: el detalle público muestra los cambios y la galería
        mockMvc.perform(get("/api/ofertas/{id}", "o-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Zelda Deluxe"))
                .andExpect(jsonPath("$.imagenes[0]").value("https://img.test/a.png"));

        // Act & Assert: sin "imagenes" la galería se conserva
        mockMvc.perform(put("/api/ofertas/{id}", "o-101")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("Zelda Deluxe 2", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagenes.length()").value(2));
    }

    @Test
    void validaPermisosDatosEImagenesAlEditar() throws Exception {
        // Sin token
        mockMvc.perform(put("/api/ofertas/{id}", "o-101")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("X", null)))
                .andExpect(status().isUnauthorized());

        // Comprador: rol equivocado
        mockMvc.perform(put("/api/ofertas/{id}", "o-101")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("X", null)))
                .andExpect(status().isForbidden());

        // Oferta que no existe
        mockMvc.perform(put("/api/ofertas/{id}", "no-existe")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("X", null)))
                .andExpect(status().isNotFound());

        // Datos de entrada inválidos: título vacío
        mockMvc.perform(put("/api/ofertas/{id}", "o-101")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("", null)))
                .andExpect(status().isBadRequest());

        // Regla de negocio: URL no http(s)
        mockMvc.perform(put("/api/ofertas/{id}", "o-101")
                        .header("Authorization", tokenVendedor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("X", "[\"ftp://img.test/a.png\"]")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void elVendedorVeElHistorialDeVentasYLoFiltra() throws Exception {
        // Arrange: juan compra y confirma o-101
        String respuesta = mockMvc.perform(post("/api/compras")
                        .header("Authorization", tokenComprador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"ofertaId\": \"o-101\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String compraId = JsonPath.read(respuesta, "$.id");
        mockMvc.perform(put("/api/compras/{id}/confirmar", compraId).header("Authorization", tokenComprador()))
                .andExpect(status().isOk());

        // Act & Assert: sin filtros aparece la venta
        mockMvc.perform(get("/api/vendedor/ventas").header("Authorization", tokenVendedor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ofertaId").value("o-101"))
                .andExpect(jsonPath("$[0].compradorId").value("u-2"))
                .andExpect(jsonPath("$[0].estado").value("COMPLETADA"));

        // Act & Assert: filtro por estado que no coincide
        mockMvc.perform(get("/api/vendedor/ventas").param("estado", "REEMBOLSADA")
                        .header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.length()").value(0));

        // Act & Assert: rango de fechas del pasado
        mockMvc.perform(get("/api/vendedor/ventas").param("desde", "2020-01-01").param("hasta", "2020-12-31")
                        .header("Authorization", tokenVendedor()))
                .andExpect(jsonPath("$.length()").value(0));

        // Act & Assert: rango inválido
        mockMvc.perform(get("/api/vendedor/ventas").param("desde", "2026-12-31").param("hasta", "2026-01-01")
                        .header("Authorization", tokenVendedor()))
                .andExpect(status().isUnprocessableEntity());

        // Act & Assert: un comprador no ve el historial del vendedor
        mockMvc.perform(get("/api/vendedor/ventas").header("Authorization", tokenComprador()))
                .andExpect(status().isForbidden());
    }
}