package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class NotificacionRepositoryEnMemoria implements NotificacionRepository {

    private final Map<String, Notificacion> notificaciones = new ConcurrentHashMap<>();

    @Override
    public void guardar(Notificacion notificacion) {
        notificaciones.put(notificacion.getId(), notificacion);
    }

    @Override
    public Optional<Notificacion> obtenerPorId(String id) {
        return Optional.ofNullable(notificaciones.get(id));
    }

    @Override
    public List<Notificacion> obtenerPorUsuarioId(String usuarioId, boolean soloNoLeidas) {
        return notificaciones.values().stream()
                .filter(n -> n.perteneceA(usuarioId))
                .filter(n -> !soloNoLeidas || !n.estaLeida())
                .sorted(Comparator.comparing(Notificacion::getFecha).reversed())
                .toList();
    }

    @Override
    public long contarNoLeidas(String usuarioId) {
        return notificaciones.values().stream().filter(n -> n.perteneceA(usuarioId) && !n.estaLeida()).count();
    }

    @Override
    public int marcarTodasLeidas(String usuarioId) {
        int marcadas = 0;
        for (Notificacion n : notificaciones.values()) {
            if (n.perteneceA(usuarioId) && !n.estaLeida()) {
                n.marcarLeida();
                marcadas++;
            }
        }
        return marcadas;
    }
}