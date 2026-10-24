package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ComentarioJpaDataRepository extends JpaRepository<ComentarioEntity, String> {
    List<ComentarioEntity> findByOfertaId(String ofertaId);
}