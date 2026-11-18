package com.uniquindio.ecommerce.infrastructure.rest;

import com.jayway.jsonpath.JsonPath;
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

// Ciclo de vida de una cuenta con seguridad, casos de uso y H2 reales: registro, login, perfil,
// cambio de contraseña y borrado lógico.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CuentaDeUsuarioIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    private void registrar(String email, String contrasena) throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Ana\", \"email\": \"" + email + "\", \"contrasena\": \"" + contrasena
                                + "\", \"telefono\": \"300\", \"rol\": \"COMPRADOR\" }"))
                .andExpect(status().isCreated());
    }

    private String login(String email, String contrasena) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"" + email + "\", \"password\": \"" + contrasena + "\" }"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(respuesta, "$.token");
    }

    private void loginRechazado(String email, String contrasena) throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"" + email + "\", \"password\": \"" + contrasena + "\" }"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elRegistroExigeUnaContrasenaSegura() throws Exception {
        // Act & Assert: sin mayúscula -> 422 con el motivo
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Ana\", \"email\": \"debil@mail.com\", \"contrasena\": \"clave1234\", \"rol\": \"COMPRADOR\" }"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("La contraseña debe incluir al menos una letra mayúscula"));

        // Act & Assert: segura -> 201
        registrar("segura@mail.com", "Clave1234");
    }

    @Test
    void elUsuarioVeYEditaSuPerfilYElEmailNoCambia() throws Exception {
        // Arrange
        registrar("perfil@mail.com", "Clave1234");
        String token = login("perfil@mail.com", "Clave1234");

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/me").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("perfil@mail.com"))
                .andExpect(jsonPath("$.rol").value("COMPRADOR"));

        mockMvc.perform(put("/api/usuarios/me").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Ana María\", \"telefono\": \"311\", \"fechaNacimiento\": \"1999-05-05\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana María"))
                .andExpect(jsonPath("$.telefono").value("311"))
                .andExpect(jsonPath("$.email").value("perfil@mail.com"));

        // Nombre vacío -> 400; sin token -> 401
        mockMvc.perform(put("/api/usuarios/me").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{ \"nombre\": \"\" }"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/usuarios/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void cambiarLaContrasenaInvalidaLaAnterior() throws Exception {
        // Arrange
        registrar("cambio@mail.com", "Clave1234");
        String token = login("cambio@mail.com", "Clave1234");

        // Act & Assert: la actual incorrecta -> 422
        mockMvc.perform(put("/api/usuarios/me/contrasena").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"contrasenaActual\": \"Incorrecta1\", \"contrasenaNueva\": \"NuevaClave9\" }"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("La contraseña actual no es correcta"));

        // Act & Assert: cambio correcto -> 204
        mockMvc.perform(put("/api/usuarios/me/contrasena").header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"contrasenaActual\": \"Clave1234\", \"contrasenaNueva\": \"NuevaClave9\" }"))
                .andExpect(status().isNoContent());

        // Assert: la anterior ya no sirve y la nueva sí
        loginRechazado("cambio@mail.com", "Clave1234");
        login("cambio@mail.com", "NuevaClave9");
    }

    @Test
    void eliminarLaCuentaImpideIniciarSesionYInvalidaElToken() throws Exception {
        // Arrange
        registrar("baja@mail.com", "Clave1234");
        String token = login("baja@mail.com", "Clave1234");

        // Act
        mockMvc.perform(delete("/api/usuarios/me").header("Authorization", token))
                .andExpect(status().isNoContent());

        // Assert: ni el login ni el token anterior funcionan
        loginRechazado("baja@mail.com", "Clave1234");
        mockMvc.perform(get("/api/usuarios/me").header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void noSePuedeEliminarLaCuentaDeUnVendedorConOfertasPublicadas() throws Exception {
        // Arrange: admin@ecommerce.com (u-1) tiene o-101 y o-102 publicadas; el seed usa la clave "clave123"
        String token = login("admin@ecommerce.com", "clave123");

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/me").header("Authorization", token))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void elCatalogoFiltraPorPrecioYCalificacionMinima() throws Exception {
        // Datos del data.sql: Zelda 59.99 (NINTENDO) y God of War 49.99 (PLAYSTATION), sin comentarios
        mockMvc.perform(get("/api/ofertas").param("precioMinimo", "55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
        mockMvc.perform(get("/api/ofertas").param("precioMaximo", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
        mockMvc.perform(get("/api/ofertas").param("precioMinimo", "40").param("precioMaximo", "60"))
                .andExpect(jsonPath("$.totalElements").value(2));

        // Sin comentarios no hay promedio, así que ninguna llega a 4 estrellas
        mockMvc.perform(get("/api/ofertas").param("calificacionMinima", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        // Filtros inválidos -> 422
        mockMvc.perform(get("/api/ofertas").param("calificacionMinima", "9"))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(get("/api/ofertas").param("precioMinimo", "50").param("precioMaximo", "10"))
                .andExpect(status().isUnprocessableEntity());
    }
}