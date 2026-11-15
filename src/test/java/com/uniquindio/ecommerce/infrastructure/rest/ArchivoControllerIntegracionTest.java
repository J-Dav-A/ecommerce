package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// En las pruebas no hay credenciales de Cloudinary: debe responder 503 y no firmar con un secreto vacío.
@SpringBootTest
@AutoConfigureMockMvc
class ArchivoControllerIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void sinCredencialesDeCloudinaryResponde503() throws Exception {
        mockMvc.perform(get("/api/archivos/firma").param("carpeta", "ofertas")
                        .header("Authorization", "Bearer " + jwtUtils.generarToken("admin@ecommerce.com", "VENDEDOR")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensaje").value("La subida de imágenes no está configurada"));
    }

    @Test
    void unaCarpetaNoPermitidaSeRechazaCon422() throws Exception {
        mockMvc.perform(get("/api/archivos/firma").param("carpeta", "otra")
                        .header("Authorization", "Bearer " + jwtUtils.generarToken("juan@gmail.com", "COMPRADOR")))
                .andExpect(status().isUnprocessableEntity());
    }
}