package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface OfertaJpaDataRepository extends JpaRepository<OfertaEntity, String> {
    List<OfertaEntity> findByEstado(EstadoOferta estado);
}