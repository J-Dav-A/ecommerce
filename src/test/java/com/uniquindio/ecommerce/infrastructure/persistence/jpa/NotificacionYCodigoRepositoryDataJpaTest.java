package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;
import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({NotificacionRepositoryJpa.class, CodigoRecuperacionRepositoryJpa.class})
class NotificacionYCodigoRepositoryDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificacionRepository notificaciones;

    @Autowired
    private CodigoRecuperacionRepository codigos;

    private final LocalDateTime base = LocalDateTime.of(2026, 11, 15, 9, 0);

    @Test
    void guardaListaCuentaYMarcaTodasComoLeidas() {
        // Arrange
        notificaciones.guardar(Notificacion.crear("n-1", "u-1", TipoNotificacion.VENTA_REALIZADA, "vieja", "c-1", base));
        notificaciones.guardar(Notificacion.crear("n-2", "u-1", TipoNotificacion.REEMBOLSO_SOLICITADO, "nueva", "c-2", base.plusHours(2)));
        notificaciones.guardar(Notificacion.crear("n-3", "u-2", TipoNotificacion.COMPRA_CONFIRMADA, "ajena", "c-3", base));
        entityManager.flush();
        entityManager.clear();

        // Assert: orden y conteo
        List<Notificacion> lista = notificaciones.obtenerPorUsuarioId("u-1", false);
        assertEquals("n-2", lista.get(0).getId());
        assertEquals(2, notificaciones.contarNoLeidas("u-1"));

        // Act
        int marcadas = notificaciones.marcarTodasLeidas("u-1");
        entityManager.clear();

        // Assert
        assertEquals(2, marcadas);
        assertEquals(0, notificaciones.contarNoLeidas("u-1"));
        assertTrue(notificaciones.obtenerPorUsuarioId("u-1", true).isEmpty());
        assertEquals(1, notificaciones.contarNoLeidas("u-2"));
    }

    @Test
    void guardarOtraVezUnaNotificacionLeidaPersisteElCambio() {
        // Arrange
        Notificacion n = Notificacion.crear("n-9", "u-1", TipoNotificacion.COMENTARIO_NUEVO, "m", "m-1", base);
        notificaciones.guardar(n);
        entityManager.flush();
        entityManager.clear();

        // Act
        Notificacion recuperada = notificaciones.obtenerPorId("n-9").orElseThrow();
        recuperada.marcarLeida();
        notificaciones.guardar(recuperada);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertTrue(notificaciones.obtenerPorId("n-9").orElseThrow().estaLeida());
    }

    @Test
    void unSoloCodigoPorUsuarioYElNuevoReemplazaAlAnterior() {
        // Arrange
        codigos.guardar(CodigoRecuperacion.crear("u-1", "hash-1", base));
        entityManager.flush();

        // Act
        CodigoRecuperacion segundo = CodigoRecuperacion.crear("u-1", "hash-2", base.plusMinutes(5));
        segundo.registrarIntentoFallido();
        codigos.guardar(segundo);
        entityManager.flush();
        entityManager.clear();

        // Assert
        CodigoRecuperacion guardado = codigos.obtenerPorUsuarioId("u-1").orElseThrow();
        assertEquals("hash-2", guardado.getCodigoHash());
        assertEquals(1, guardado.getIntentos());

        // Act
        codigos.eliminar("u-1");
        entityManager.flush();

        // Assert
        assertTrue(codigos.obtenerPorUsuarioId("u-1").isEmpty());
    }
}