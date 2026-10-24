package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CompraJpaDataRepository extends JpaRepository<CompraEntity, String> {
    List<CompraEntity> findByCompradorIdOrderByFechaCompraDesc(String compradorId);
    boolean existsByCompradorIdAndOfertaIdAndEstado(String compradorId, String ofertaId, EstadoCompra estado);
}