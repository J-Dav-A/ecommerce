package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ClaveDigitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ClaveDigitalJpaDataRepository extends JpaRepository<ClaveDigitalEntity, String> {
    Optional<ClaveDigitalEntity> findFirstByOfertaIdAndEstado(String ofertaId, EstadoClaveDigital estado);
}