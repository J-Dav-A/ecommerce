package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;
import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.service.EnviadorCorreo;
import com.uniquindio.ecommerce.infrastructure.persistence.CodigoRecuperacionRepositoryEnMemoria;
import com.uniquindio.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecuperacionDeContrasenaTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UsuarioRepository usuarios = new UsuarioRepositoryEnMemoria();
    private final CodigoRecuperacionRepository codigos = new CodigoRecuperacionRepositoryEnMemoria();
    private final List<String> correos = new ArrayList<>();
    private final EnviadorCorreo enviador = (para, asunto, cuerpo) -> correos.add(para + "|" + cuerpo);

    private final SolicitarRecuperacionContrasena solicitar =
            new SolicitarRecuperacionContrasena(usuarios, codigos, encoder, enviador, () -> "123456");
    private final RestablecerContrasena restablecer = new RestablecerContrasena(usuarios, codigos, encoder);

    @BeforeEach
    void preparar() {
        usuarios.guardar(Usuario.reconstruir("u-1", "Ana", "ana@mail.com", encoder.encode("ClaveVieja1"),
                "300", "COMPRADOR", LocalDate.of(2000, 1, 1)));
    }

    @Test
    void elCodigoSeEnviaPorCorreoYSeGuardaCifrado() {
        // Act
        solicitar.ejecutar("ana@mail.com");

        // Assert
        assertEquals(1, correos.size());
        assertTrue(correos.get(0).contains("123456"));
        String hash = codigos.obtenerPorUsuarioId("u-1").orElseThrow().getCodigoHash();
        assertFalse(hash.contains("123456"));
    }

    @Test
    void conUnCorreoDesconocidoNoHaceNadaNiFalla() {
        // Act
        solicitar.ejecutar("nadie@mail.com");

        // Assert
        assertTrue(correos.isEmpty());
        assertTrue(codigos.obtenerPorUsuarioId("u-1").isEmpty());
    }

    @Test
    void siElCorreoFallaLaSolicitudNoRevelaNada() {
        // Arrange
        SolicitarRecuperacionContrasena conFallo = new SolicitarRecuperacionContrasena(usuarios, codigos, encoder,
                (p, a, c) -> { throw new IllegalStateException("SMTP caído"); }, () -> "123456");

        // Act & Assert: no lanza
        conFallo.ejecutar("ana@mail.com");
    }

    @Test
    void conElCodigoCorrectoCambiaLaContrasenaYElCodigoSeUsaUnaSolaVez() {
        // Arrange
        solicitar.ejecutar("ana@mail.com");

        // Act
        restablecer.ejecutar("ana@mail.com", "123456", "ClaveNueva1");

        // Assert
        assertTrue(encoder.matches("ClaveNueva1", usuarios.obtenerPorId("u-1").orElseThrow().getContrasena()));
        assertTrue(codigos.obtenerPorUsuarioId("u-1").isEmpty());
        assertThrows(ReglaDominioException.class, () -> restablecer.ejecutar("ana@mail.com", "123456", "ClaveOtra12"));
    }

    @Test
    void unCodigoErradoSumaIntentosYTrasCincoYaNoSirveNiElCorrecto() {
        // Arrange
        solicitar.ejecutar("ana@mail.com");

        // Act
        for (int i = 0; i < CodigoRecuperacion.MAX_INTENTOS; i++) {
            assertThrows(ReglaDominioException.class, () -> restablecer.ejecutar("ana@mail.com", "000000", "ClaveNueva1"));
        }

        // Assert: bloqueado aun con el código correcto
        ReglaDominioException e = assertThrows(ReglaDominioException.class,
                () -> restablecer.ejecutar("ana@mail.com", "123456", "ClaveNueva1"));
        assertEquals("El código no es válido o expiró", e.getMessage());
        assertTrue(encoder.matches("ClaveVieja1", usuarios.obtenerPorId("u-1").orElseThrow().getContrasena()));
    }

    @Test
    void unCodigoVencidoSeRechazaConElMismoMensajeGenerico() {
        // Arrange: código creado hace 20 minutos
        codigos.guardar(CodigoRecuperacion.crear("u-1", encoder.encode("123456"), LocalDateTime.now().minusMinutes(20)));

        // Act & Assert
        ReglaDominioException e = assertThrows(ReglaDominioException.class,
                () -> restablecer.ejecutar("ana@mail.com", "123456", "ClaveNueva1"));
        assertEquals("El código no es válido o expiró", e.getMessage());
    }

    @Test
    void laContrasenaNuevaDebeCumplirLaPoliticaYElCodigoSigueVigente() {
        // Arrange
        solicitar.ejecutar("ana@mail.com");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> restablecer.ejecutar("ana@mail.com", "123456", "debil"));
        assertTrue(codigos.obtenerPorUsuarioId("u-1").isPresent());
    }

    @Test
    void conUnCorreoDesconocidoElErrorEsElMismoQueConUnCodigoMalo() {
        // Act & Assert
        ReglaDominioException e = assertThrows(ReglaDominioException.class,
                () -> restablecer.ejecutar("nadie@mail.com", "123456", "ClaveNueva1"));
        assertEquals("El código no es válido o expiró", e.getMessage());
    }
}