package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.TipoNotificacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacionYCodigoRecuperacionTest {

    private final LocalDateTime ahora = LocalDateTime.of(2026, 11, 15, 10, 0);

    @Test
    void laNotificacionNaceSinLeerYSePuedeMarcarComoLeida() {
        // Arrange
        Notificacion n = Notificacion.crear("n-1", "u-1", TipoNotificacion.VENTA_REALIZADA, "Nueva compra", "c-1", ahora);

        // Assert
        assertFalse(n.estaLeida());
        assertTrue(n.perteneceA("u-1"));
        assertFalse(n.perteneceA("u-2"));

        // Act
        n.marcarLeida();

        // Assert
        assertTrue(n.estaLeida());
    }

    @Test
    void laNotificacionExigeDestinatarioTipoYMensaje() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> Notificacion.crear("n-1", " ", TipoNotificacion.VENTA_REALIZADA, "m", null, ahora));
        assertThrows(ReglaDominioException.class,
                () -> Notificacion.crear("n-1", "u-1", null, "m", null, ahora));
        assertThrows(ReglaDominioException.class,
                () -> Notificacion.crear("n-1", "u-1", TipoNotificacion.VENTA_REALIZADA, "", null, ahora));
    }

    @Test
    void elCodigoVale15MinutosExactos() {
        // Arrange
        CodigoRecuperacion codigo = CodigoRecuperacion.crear("u-1", "hash", ahora);

        // Assert
        assertTrue(codigo.estaVigente(ahora.plusMinutes(14)));
        assertFalse(codigo.estaVigente(ahora.plusMinutes(15)));
    }

    @Test
    void elCodigoSeInvalidaTrasCincoIntentosFallidos() {
        // Arrange
        CodigoRecuperacion codigo = CodigoRecuperacion.crear("u-1", "hash", ahora);

        // Act
        for (int i = 0; i < CodigoRecuperacion.MAX_INTENTOS - 1; i++) codigo.registrarIntentoFallido();
        assertTrue(codigo.estaVigente(ahora));
        codigo.registrarIntentoFallido();

        // Assert
        assertFalse(codigo.estaVigente(ahora));
    }
}