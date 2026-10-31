package com.uniquindio.ecommerce.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Ruta protegida elegida: DELETE /api/ofertas/{id}, que SecurityConfig restringe solo al rol VENDEDOR.
// Se carga la aplicación completa (@SpringBootTest) para que se aplique la SecurityFilterChain REAL;
// con @WebMvcTest esa configuración no se carga.
// Los tokens se generan con el JwtUtils real y los usuarios salen del data.sql:
//   admin@ecommerce.com -> VENDEDOR   |   juan@gmail.com -> COMPRADOR
// @Transactional deshace el borrado de la prueba del 200 para no afectar a las demás.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeguridadEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void sinTokenDeberiaResponder401() throws Exception {
        // Arrange: no se envía ningún header Authorization

        // Act & Assert
        mockMvc.perform(delete("/api/ofertas/{id}", "o-102"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenDeCompradorDeberiaResponder403() throws Exception {
        // Arrange: token válido, pero de un COMPRADOR (la ruta exige VENDEDOR)
        String token = jwtUtils.generarToken("juan@gmail.com", "COMPRADOR");

        // Act & Assert
        mockMvc.perform(delete("/api/ofertas/{id}", "o-102")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void conTokenDeVendedorDeberiaResponder200() throws Exception {
        // Arrange: token válido de un VENDEDOR
        String token = jwtUtils.generarToken("admin@ecommerce.com", "VENDEDOR");

        // Act & Assert
        mockMvc.perform(delete("/api/ofertas/{id}", "o-102")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}