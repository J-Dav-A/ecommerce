package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// juan@gmail.com (u-2, COMPRADOR) guarda favoritos; o-101 y o-102 vienen del data.sql.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoritosIntegracionTest {

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

    @Test
    void elCompradorAgregaListaYQuitaFavoritos() throws Exception {
        // Act & Assert: agregar
        mockMvc.perform(post("/api/favoritos/{id}", "o-101").header("Authorization", tokenComprador()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("o-101"));
        mockMvc.perform(post("/api/favoritos/{id}", "o-102").header("Authorization", tokenComprador()))
                .andExpect(status().isCreated());

        // Act & Assert: la lista trae las dos ofertas completas
        mockMvc.perform(get("/api/favoritos").header("Authorization", tokenComprador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").exists());

        // Act & Assert: quitar uno
        mockMvc.perform(delete("/api/favoritos/{id}", "o-101").header("Authorization", tokenComprador()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/favoritos").header("Authorization", tokenComprador()))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("o-102"));
    }

    @Test
    void validaDuplicadosInexistentesYPermisos() throws Exception {
        // Duplicado -> 422
        mockMvc.perform(post("/api/favoritos/{id}", "o-101").header("Authorization", tokenComprador()))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/favoritos/{id}", "o-101").header("Authorization", tokenComprador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("La oferta ya está en tus favoritos"));

        // Oferta inexistente -> 404; quitar lo que no es favorito -> 404
        mockMvc.perform(post("/api/favoritos/{id}", "no-existe").header("Authorization", tokenComprador()))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/favoritos/{id}", "o-102").header("Authorization", tokenComprador()))
                .andExpect(status().isNotFound());

        // Sin token -> 401; vendedor (rol equivocado) -> 403
        mockMvc.perform(get("/api/favoritos")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/favoritos").header("Authorization", tokenVendedor()))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/favoritos/{id}", "o-101").header("Authorization", tokenVendedor()))
                .andExpect(status().isForbidden());
    }
}