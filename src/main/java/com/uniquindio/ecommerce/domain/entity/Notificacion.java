package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.Objects;

// Aviso para un usuario dentro de la aplicación. referenciaId apunta a la compra o al comentario que lo originó.
public class Notificacion {

    private final String id;
    private final String usuarioId;
    private final TipoNotificacion tipo;
    private final String mensaje;
    private final String referenciaId;
    private final LocalDateTime fecha;
    private boolean leida;

    private Notificacion(String id, String usuarioId, TipoNotificacion tipo, String mensaje,
                         String referenciaId, LocalDateTime fecha, boolean leida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.referenciaId = referenciaId;
        this.fecha = fecha;
        this.leida = leida;
    }

    public static Notificacion crear(String id, String usuarioId, TipoNotificacion tipo, String mensaje,
                                     String referenciaId, LocalDateTime fecha) {
        if (id == null || id.isBlank()) throw new ReglaDominioException("La notificación requiere identificador");
        if (usuarioId == null || usuarioId.isBlank()) throw new ReglaDominioException("La notificación requiere destinatario");
        if (tipo == null) throw new ReglaDominioException("La notificación requiere tipo");
        if (mensaje == null || mensaje.isBlank()) throw new ReglaDominioException("La notificación requiere mensaje");
        if (fecha == null) throw new ReglaDominioException("La notificación requiere fecha");
        return new Notificacion(id, usuarioId, tipo, mensaje, referenciaId, fecha, false);
    }

    public static Notificacion reconstruir(String id, String usuarioId, TipoNotificacion tipo, String mensaje,
                                           String referenciaId, LocalDateTime fecha, boolean leida) {
        return new Notificacion(id, usuarioId, tipo, mensaje, referenciaId, fecha, leida);
    }

    public void marcarLeida() { this.leida = true; }

    public boolean perteneceA(String usuarioId) { return this.usuarioId.equals(usuarioId); }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public TipoNotificacion getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public String getReferenciaId() { return referenciaId; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean estaLeida() { return leida; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificacion otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}