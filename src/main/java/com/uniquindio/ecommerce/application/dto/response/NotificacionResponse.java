package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Notificacion;

import java.time.LocalDateTime;

public record NotificacionResponse(String id, String tipo, String mensaje, String referenciaId,
                                   LocalDateTime fecha, boolean leida) {
    public static NotificacionResponse desde(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getTipo().name(), n.getMensaje(), n.getReferenciaId(),
                n.getFecha(), n.estaLeida());
    }
}