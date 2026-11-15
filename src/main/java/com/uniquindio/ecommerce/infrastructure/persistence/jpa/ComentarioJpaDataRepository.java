package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

@Repository
public interface ComentarioJpaDataRepository extends JpaRepository<ComentarioEntity, String> {

    List<ComentarioEntity> findByOfertaIdOrderByFechaDesc(String ofertaId);

    @Query("SELECT new com.uniquindio.ecommerce.domain.repository.ResumenPorOferta(m.ofertaId, AVG(m.calificacion), COUNT(m)) " +
            "FROM ComentarioEntity m WHERE m.ofertaId IN :ofertaIds GROUP BY m.ofertaId")
    List<ResumenPorOferta> resumenPorOfertas(@Param("ofertaIds") Collection<String> ofertaIds);

    boolean existsByCompraId(String compraId);
}