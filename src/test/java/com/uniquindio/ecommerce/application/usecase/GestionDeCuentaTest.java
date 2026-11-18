package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.CompraRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.OfertaRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestionDeCuentaTest {

    private final UsuarioRepository usuarioRepository = new UsuarioRepositoryEnMemoria();
    private final OfertaRepository ofertaRepository = new OfertaRepositoryEnMemoria();
    private final CompraRepository compraRepository = new CompraRepositoryEnMemoria();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private final ConsultarPerfil consultarPerfil = new ConsultarPerfil(usuarioRepository);
    private final EditarPerfil editarPerfil = new EditarPerfil(usuarioRepository);
    private final CambiarContrasena cambiarContrasena = new CambiarContrasena(usuarioRepository, encoder);
    private final EliminarCuenta eliminarCuenta = new EliminarCuenta(usuarioRepository, ofertaRepository, compraRepository);

    private final Precio precio = new Precio(new BigDecimal("30"), "USD");

    @BeforeEach
    void preparar() {
        usuarioRepository.guardar(Usuario.crear("u-1", "Ana", "ana@mail.com", encoder.encode("Clave123"),
                "300", "COMPRADOR", LocalDate.of(2000, 1, 1)));
    }

    @Test
    void consultaYEditaElPerfilPropio() {
        // Act
        editarPerfil.ejecutar("u-1", "Ana María", "311", LocalDate.of(1999, 5, 5));

        // Assert
        Usuario perfil = consultarPerfil.ejecutar("u-1");
        assertEquals("Ana María", perfil.getNombre());
        assertEquals("311", perfil.getTelefono());
        assertEquals("ana@mail.com", perfil.getEmail());
    }

    @Test
    void unUsuarioQueNoExisteSeTrataComoNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> consultarPerfil.ejecutar("u-99"));
        assertThrows(RecursoNoEncontradoException.class, () -> editarPerfil.ejecutar("u-99", "X", null, null));
    }

    @Test
    void cambiaLaContrasenaYLaNuevaQuedaCifrada() {
        // Act
        cambiarContrasena.ejecutar("u-1", "Clave123", "NuevaClave9");

        // Assert
        String guardada = usuarioRepository.obtenerPorId("u-1").orElseThrow().getContrasena();
        assertTrue(encoder.matches("NuevaClave9", guardada));
        assertFalse(encoder.matches("Clave123", guardada));
    }

    @Test
    void rechazaElCambioConContrasenaActualIncorrecta() {
        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> cambiarContrasena.ejecutar("u-1", "otraCosa1", "NuevaClave9"));

        // Assert
        assertEquals("La contraseña actual no es correcta", excepcion.getMessage());
        assertTrue(encoder.matches("Clave123", usuarioRepository.obtenerPorId("u-1").orElseThrow().getContrasena()));
    }

    @Test
    void rechazaUnaContrasenaNuevaDebilOIgualALaActual() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> cambiarContrasena.ejecutar("u-1", "Clave123", "corta"));
        assertThrows(ReglaDominioException.class, () -> cambiarContrasena.ejecutar("u-1", "Clave123", "sinnumeroA"));
        ReglaDominioException igual = assertThrows(ReglaDominioException.class,
                () -> cambiarContrasena.ejecutar("u-1", "Clave123", "Clave123"));
        assertEquals("La contraseña nueva debe ser distinta de la actual", igual.getMessage());
    }

    @Test
    void eliminarLaCuentaHaceBorradoLogicoYYaNoSePuedeUsar() {
        // Act
        eliminarCuenta.ejecutar("u-1");

        // Assert: el registro sigue guardado pero inactivo, y las operaciones lo tratan como inexistente
        assertFalse(usuarioRepository.obtenerPorId("u-1").orElseThrow().estaActivo());
        assertThrows(RecursoNoEncontradoException.class, () -> consultarPerfil.ejecutar("u-1"));
        assertThrows(RecursoNoEncontradoException.class, () -> eliminarCuenta.ejecutar("u-1"));
    }

    @Test
    void noPermiteEliminarLaCuentaConComprasPendientes() {
        // Arrange
        compraRepository.guardar(Compra.reconstruir("c-1", "o-1", "u-1", precio, LocalDateTime.now(), EstadoCompra.PENDIENTE));

        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () -> eliminarCuenta.ejecutar("u-1"));

        // Assert
        assertEquals("No puedes eliminar la cuenta con compras pendientes de confirmar", excepcion.getMessage());
        assertTrue(usuarioRepository.obtenerPorId("u-1").orElseThrow().estaActivo());
    }

    @Test
    void noPermiteEliminarLaCuentaDeUnVendedorConOfertasPublicadas() {
        // Arrange
        usuarioRepository.guardar(Usuario.crear("v-1", "Vendedor", "v@mail.com", "hash", null, "VENDEDOR", null));
        Oferta oferta = Oferta.crear("o-1", "v-1", "Juego", precio, 3);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
        ofertaRepository.guardar(oferta);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> eliminarCuenta.ejecutar("v-1"));

        // Al eliminar la oferta ya puede borrar la cuenta
        oferta.eliminar();
        ofertaRepository.guardar(oferta);
        eliminarCuenta.ejecutar("v-1");
        assertFalse(usuarioRepository.obtenerPorId("v-1").orElseThrow().estaActivo());
    }
}