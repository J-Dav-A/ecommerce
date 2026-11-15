package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarNotificaciones {

    private final NotificacionRepository notificacionRepository;

    @Transactional(readOnly = true)
    public List<Notificacion> listar(String usuarioId, boolean soloNoLeidas) {
        return notificacionRepository.obtenerPorUsuarioId(usuarioId, soloNoLeidas);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(String usuarioId) {
        return notificacionRepository.contarNoLeidas(usuarioId);
    }
}