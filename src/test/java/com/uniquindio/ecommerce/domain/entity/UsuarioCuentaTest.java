package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioCuentaTest {

    private Usuario nuevo() {
        return Usuario.crear("u-1", "Ana", "ana@mail.com", "hash", "300", "COMPRADOR", LocalDate.of(2000, 1, 1));
    }

    @Test
    void unUsuarioNuevoQuedaActivo() {
        assertTrue(nuevo().estaActivo());
    }

    @Test
    void editarPerfilCambiaNombreTelefonoYFechaPeroNoElEmailNiElRol() {
        // Arrange
        Usuario usuario = nuevo();

        // Act
        usuario.editarPerfil("Ana María", "311", LocalDate.of(1999, 5, 5));

        // Assert
        assertEquals("Ana María", usuario.getNombre());
        assertEquals("311", usuario.getTelefono());
        assertEquals(LocalDate.of(1999, 5, 5), usuario.getFechaNacimiento());
        assertEquals("ana@mail.com", usuario.getEmail());
        assertEquals("COMPRADOR", usuario.getRol());
    }

    @Test
    void editarPerfilRechazaNombreVacioYFechaFutura() {
        // Arrange
        Usuario usuario = nuevo();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> usuario.editarPerfil(" ", "311", null));
        assertThrows(ReglaDominioException.class, () -> usuario.editarPerfil("Ana", "311", LocalDate.now().plusDays(1)));
        assertEquals("Ana", usuario.getNombre());
    }

    @Test
    void desactivarHaceElBorradoLogicoYNoSePuedeRepetir() {
        // Arrange
        Usuario usuario = nuevo();

        // Act
        usuario.desactivar();

        // Assert
        assertFalse(usuario.estaActivo());
        assertThrows(ReglaDominioException.class, usuario::desactivar);
    }
}