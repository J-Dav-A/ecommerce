package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarcarNotificaciones {

    private final NotificacionRepository notificacionRepository;

    @Transactional
    public void marcarLeida(String notificacionId, String usuarioId) {
        // La notificación de otro usuario se trata como inexistente
        Notificacion notificacion = notificacionRepository.obtenerPorId(notificacionId)
                .filter(n -> n.perteneceA(usuarioId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La notificación no existe"));
        notificacion.marcarLeida();
        notificacionRepository.guardar(notificacion);
    }

    @Transactional
    public int marcarTodasLeidas(String usuarioId) {
        return notificacionRepository.marcarTodasLeidas(usuarioId);
    }
}