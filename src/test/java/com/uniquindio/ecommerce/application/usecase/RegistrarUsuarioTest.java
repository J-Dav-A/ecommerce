package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarUsuarioTest {

    private final UsuarioRepository usuarioRepository = new UsuarioRepositoryEnMemoria();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RegistrarUsuario registrarUsuario = new RegistrarUsuario(usuarioRepository, passwordEncoder);

    @Test
    void deberiaGuardarElUsuarioConLaContrasenaCifrada() {
        // Act
        Usuario usuario = registrarUsuario.ejecutar("Ana", "ana@mail.com", "Clave123", "3000000000",
                "COMPRADOR", LocalDate.of(2000, 1, 1));

        // Assert
        Usuario guardado = usuarioRepository.obtenerPorId(usuario.getId()).orElseThrow();
        assertNotEquals("Clave123", guardado.getContrasena());
        assertTrue(passwordEncoder.matches("Clave123", guardado.getContrasena()));
    }

    @Test
    void noDebeRegistrarUnEmailYaRegistrado() {
        // Arrange
        registrarUsuario.ejecutar("Ana", "ana@mail.com", "Clave123", "3000000000",
                "COMPRADOR", LocalDate.of(2000, 1, 1));

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> registrarUsuario.ejecutar("Otra Ana", "ana@mail.com", "otraClave", "3111111111",
                        "VENDEDOR", LocalDate.of(1999, 5, 5)));

        // Assert
        assertEquals("El email ya se encuentra registrado", excepcion.getMessage());
    }

    @Test
    void noDebeRegistrarUnUsuarioSinNombre() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> registrarUsuario.ejecutar(" ", "sin.nombre@mail.com", "Clave123", "3000000000",
                        "COMPRADOR", LocalDate.of(2000, 1, 1)));
        assertTrue(!usuarioRepository.existePorEmail("sin.nombre@mail.com"));
    }
}