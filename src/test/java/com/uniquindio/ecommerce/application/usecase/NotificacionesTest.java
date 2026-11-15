package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.TipoNotificacion;
import com.uniquindio.ecommerce.infrastructure.persistence.NotificacionRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacionesTest {

    private final NotificacionRepository repositorio = new NotificacionRepositoryEnMemoria();
    private final ConsultarNotificaciones consultar = new ConsultarNotificaciones(repositorio);
    private final MarcarNotificaciones marcar = new MarcarNotificaciones(repositorio);
    private final LocalDateTime base = LocalDateTime.of(2026, 11, 15, 9, 0);

    @BeforeEach
    void preparar() {
        repositorio.guardar(Notificacion.crear("n-1", "u-1", TipoNotificacion.VENTA_REALIZADA, "vieja", "c-1", base));
        repositorio.guardar(Notificacion.crear("n-2", "u-1", TipoNotificacion.COMENTARIO_NUEVO, "nueva", "m-1", base.plusHours(1)));
        repositorio.guardar(Notificacion.crear("n-3", "u-2", TipoNotificacion.COMPRA_CONFIRMADA, "ajena", "c-2", base));
    }

    @Test
    void listaSoloLasDelUsuarioConLaMasRecientePrimero() {
        // Act
        List<Notificacion> lista = consultar.listar("u-1", false);

        // Assert
        assertEquals(List.of("n-2", "n-1"), lista.stream().map(Notificacion::getId).toList());
        assertEquals(2, consultar.contarNoLeidas("u-1"));
    }

    @Test
    void marcarUnaComoLeidaLaQuitaDeLasNoLeidas() {
        // Act
        marcar.marcarLeida("n-2", "u-1");

        // Assert
        assertEquals(1, consultar.contarNoLeidas("u-1"));
        assertEquals(List.of("n-1"), consultar.listar("u-1", true).stream().map(Notificacion::getId).toList());
    }

    @Test
    void laNotificacionDeOtroUsuarioSeTrataComoInexistente() {
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> marcar.marcarLeida("n-3", "u-1"));
        assertThrows(RecursoNoEncontradoException.class, () -> marcar.marcarLeida("n-999", "u-1"));
        assertEquals(1, consultar.contarNoLeidas("u-2"));
    }

    @Test
    void marcarTodasSoloAfectaAlUsuario() {
        // Act
        int marcadas = marcar.marcarTodasLeidas("u-1");

        // Assert
        assertEquals(2, marcadas);
        assertEquals(0, consultar.contarNoLeidas("u-1"));
        assertEquals(1, consultar.contarNoLeidas("u-2"));
        assertTrue(consultar.listar("u-1", true).isEmpty());
    }
}