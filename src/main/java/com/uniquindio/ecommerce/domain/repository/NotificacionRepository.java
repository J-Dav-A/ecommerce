package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Notificacion;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepository {
    void guardar(Notificacion notificacion);
    Optional<Notificacion> obtenerPorId(String id);
    // La más reciente primero
    List<Notificacion> obtenerPorUsuarioId(String usuarioId, boolean soloNoLeidas);
    long contarNoLeidas(String usuarioId);
    // Devuelve cuántas se marcaron
    int marcarTodasLeidas(String usuarioId);
}