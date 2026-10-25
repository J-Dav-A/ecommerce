package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraJpaDataRepository extends JpaRepository<CompraEntity, String> {

    boolean existsByCompradorIdAndOfertaIdAndEstado(String compradorId, String ofertaId, EstadoCompra estado);

    List<CompraEntity> findByCompradorIdOrderByFechaCompraDesc(String compradorId);

    @Query("SELECT c FROM CompraEntity c WHERE c.compradorId = :compradorId ORDER BY c.fechaCompra DESC")
    List<CompraEntity> obtenerHistorialPorComprador(@Param("compradorId") String compradorId);
}