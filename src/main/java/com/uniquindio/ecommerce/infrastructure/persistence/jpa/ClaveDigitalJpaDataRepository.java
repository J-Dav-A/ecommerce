package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ClaveDigitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaveDigitalJpaDataRepository extends JpaRepository<ClaveDigitalEntity, String> {

    Optional<ClaveDigitalEntity> findFirstByOfertaIdAndEstado(String ofertaId, EstadoClaveDigital estado);

    @Query("SELECT c FROM ClaveDigitalEntity c WHERE c.oferta.id = :ofertaId AND c.estado = :estado")
    List<ClaveDigitalEntity> buscarClavesPorOfertaYEstado(@Param("ofertaId") String ofertaId,
                                                          @Param("estado") EstadoClaveDigital estado);

    @Query("SELECT COUNT(c) FROM ClaveDigitalEntity c WHERE c.oferta.id = :ofertaId AND c.estado = 'DISPONIBLE'")
    long contarClavesDisponibles(@Param("ofertaId") String ofertaId);
}