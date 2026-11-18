package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionJpaDataRepository extends JpaRepository<NotificacionEntity, String> {

    List<NotificacionEntity> findByUsuarioIdOrderByFechaDesc(String usuarioId);

    List<NotificacionEntity> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(String usuarioId);

    long countByUsuarioIdAndLeidaFalse(String usuarioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE NotificacionEntity n SET n.leida = true WHERE n.usuarioId = :usuarioId AND n.leida = false")
    int marcarTodasLeidas(@Param("usuarioId") String usuarioId);
}