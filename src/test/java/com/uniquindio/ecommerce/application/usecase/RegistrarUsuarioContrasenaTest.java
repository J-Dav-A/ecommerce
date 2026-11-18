package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarUsuarioContrasenaTest {

    private final UsuarioRepository usuarioRepository = new UsuarioRepositoryEnMemoria();
    private final RegistrarUsuario registrarUsuario = new RegistrarUsuario(usuarioRepository, new BCryptPasswordEncoder());

    @Test
    void noDebeRegistrarConUnaContrasenaQueIncumpleLaPolitica() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> registrarUsuario.ejecutar("Ana", "ana@mail.com", "clave123", null, "COMPRADOR", null)); // sin mayúscula
        assertThrows(ReglaDominioException.class,
                () -> registrarUsuario.ejecutar("Ana", "ana@mail.com", "ClaveSegura", null, "COMPRADOR", null)); // sin número
        assertThrows(ReglaDominioException.class,
                () -> registrarUsuario.ejecutar("Ana", "ana@mail.com", "Cl1", null, "COMPRADOR", null)); // muy corta
        assertFalse(usuarioRepository.existePorEmail("ana@mail.com"));
    }
}
