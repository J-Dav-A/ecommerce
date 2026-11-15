package com.uniquindio.ecommerce.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// El frontend necesita el rol para armar el menú y proteger rutas por rol.
// Usuarios semilla: admin@ecommerce.com (u-1, VENDEDOR) y juan@gmail.com (u-2, COMPRADOR), clave123.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginDevuelveRolIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    private org.springframework.test.web.servlet.ResultActions login(String email, String clave) throws Exception {
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"" + email + "\", \"password\": \"" + clave + "\" }"));
    }

    @Test
    void elLoginDelVendedorDevuelveSuRolYSusDatos() throws Exception {
        login("admin@ecommerce.com", "clave123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("admin@ecommerce.com"))
                .andExpect(jsonPath("$.rol").value("VENDEDOR"))
                .andExpect(jsonPath("$.usuarioId").value("u-1"))
                .andExpect(jsonPath("$.nombre").value("Admin Gamer"));
    }

    @Test
    void elLoginDelCompradorDevuelveRolComprador() throws Exception {
        login("juan@gmail.com", "clave123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("COMPRADOR"))
                .andExpect(jsonPath("$.usuarioId").value("u-2"));
    }

    @Test
    void conUnaContrasenaIncorrectaResponde401() throws Exception {
        login("juan@gmail.com", "incorrecta1")
                .andExpect(status().isUnauthorized());
    }
}