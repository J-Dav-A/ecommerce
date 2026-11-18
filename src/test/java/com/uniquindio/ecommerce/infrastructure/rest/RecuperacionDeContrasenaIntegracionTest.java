package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.domain.service.GeneradorDeCodigo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Recuperación de contraseña con seguridad, casos de uso y H2 reales. El código se fija en 123456 para poder probarlo.
// Usuario semilla: juan@gmail.com con clave123.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecuperacionDeContrasenaIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeneradorDeCodigo generadorDeCodigo;

    @BeforeEach
    void fijarCodigo() {
        when(generadorDeCodigo.nuevo()).thenReturn("123456");
    }

    private void pedirCodigo(String email) throws Exception {
        mockMvc.perform(post("/api/auth/recuperar").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"" + email + "\" }"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("Si el correo está registrado, te enviamos un código de 6 dígitos"));
    }

    private org.springframework.test.web.servlet.ResultActions restablecer(String email, String codigo, String nueva) throws Exception {
        return mockMvc.perform(post("/api/auth/restablecer").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"" + email + "\", \"codigo\": \"" + codigo
                        + "\", \"contrasenaNueva\": \"" + nueva + "\" }"));
    }

    private org.springframework.test.web.servlet.ResultActions login(String email, String clave) throws Exception {
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"" + email + "\", \"password\": \"" + clave + "\" }"));
    }

    @Test
    void conElCodigoCorrectoSeCambiaLaContrasenaYLaVieja_yaNoSirve() throws Exception {
        // Arrange
        pedirCodigo("juan@gmail.com");

        // Act
        restablecer("juan@gmail.com", "123456", "NuevaClave1").andExpect(status().isNoContent());

        // Assert
        login("juan@gmail.com", "NuevaClave1").andExpect(status().isOk());
        login("juan@gmail.com", "clave123").andExpect(status().isUnauthorized());
    }

    @Test
    void elMismoCodigoNoSirveDosVeces() throws Exception {
        // Arrange
        pedirCodigo("juan@gmail.com");
        restablecer("juan@gmail.com", "123456", "NuevaClave1").andExpect(status().isNoContent());

        // Act & Assert
        restablecer("juan@gmail.com", "123456", "OtraClave12")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("El código no es válido o expiró"));
    }

    @Test
    void pedirCodigoDeUnCorreoInexistenteResponde202IgualYNoRevelaNada() throws Exception {
        // Act & Assert
        pedirCodigo("fantasma@mail.com");
        restablecer("fantasma@mail.com", "123456", "NuevaClave1")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("El código no es válido o expiró"));
    }

    @Test
    void trasCincoCodigosErradosYaNoSirveNiElCorrecto() throws Exception {
        // Arrange
        pedirCodigo("juan@gmail.com");

        // Act
        for (int i = 0; i < 5; i++) {
            restablecer("juan@gmail.com", "000000", "NuevaClave1").andExpect(status().isUnprocessableEntity());
        }

        // Assert
        restablecer("juan@gmail.com", "123456", "NuevaClave1").andExpect(status().isUnprocessableEntity());
        login("juan@gmail.com", "clave123").andExpect(status().isOk());
    }

    @Test
    void validaElFormatoDeLosDatos() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/recuperar").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"no-es-correo\" }"))
                .andExpect(status().isBadRequest());
        restablecer("juan@gmail.com", "12", "NuevaClave1").andExpect(status().isBadRequest());
    }

    @Test
    void laContrasenaNuevaDebeSerSegura() throws Exception {
        // Arrange
        pedirCodigo("juan@gmail.com");

        // Act & Assert
        restablecer("juan@gmail.com", "123456", "debil").andExpect(status().isUnprocessableEntity());
    }
}