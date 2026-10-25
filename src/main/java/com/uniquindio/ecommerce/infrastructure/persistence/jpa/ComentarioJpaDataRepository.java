package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioJpaDataRepository extends JpaRepository<ComentarioEntity, String> {

    List<ComentarioEntity> findByOfertaId(String ofertaId);

    @Query("SELECT AVG(c.calificacion) FROM ComentarioEntity c WHERE c.ofertaId = :ofertaId")
    Double obtenerCalificacionPromedioPorOferta(@Param("ofertaId") String ofertaId);
}