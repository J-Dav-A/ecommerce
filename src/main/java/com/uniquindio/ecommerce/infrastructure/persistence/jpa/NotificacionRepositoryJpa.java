package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.NotificacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificacionRepositoryJpa implements NotificacionRepository {

    private final NotificacionJpaDataRepository dataRepository;

    @Override
    public void guardar(Notificacion n) {
        NotificacionEntity entity = new NotificacionEntity();
        entity.setId(n.getId());
        entity.setUsuarioId(n.getUsuarioId());
        entity.setTipo(n.getTipo());
        entity.setMensaje(n.getMensaje());
        entity.setReferenciaId(n.getReferenciaId());
        entity.setFecha(n.getFecha());
        entity.setLeida(n.estaLeida());
        dataRepository.save(entity);
    }

    @Override
    public Optional<Notificacion> obtenerPorId(String id) {
        return dataRepository.findById(id).map(this::aDominio);
    }

    @Override
    public List<Notificacion> obtenerPorUsuarioId(String usuarioId, boolean soloNoLeidas) {
        List<NotificacionEntity> entidades = soloNoLeidas
                ? dataRepository.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId)
                : dataRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
        return entidades.stream().map(this::aDominio).toList();
    }

    @Override
    public long contarNoLeidas(String usuarioId) {
        return dataRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    @Transactional
    public int marcarTodasLeidas(String usuarioId) {
        return dataRepository.marcarTodasLeidas(usuarioId);
    }

    private Notificacion aDominio(NotificacionEntity e) {
        return Notificacion.reconstruir(e.getId(), e.getUsuarioId(), e.getTipo(), e.getMensaje(),
                e.getReferenciaId(), e.getFecha(), e.isLeida());
    }
}