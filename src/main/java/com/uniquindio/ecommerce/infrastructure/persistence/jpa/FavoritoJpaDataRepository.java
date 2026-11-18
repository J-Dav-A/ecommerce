package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoJpaDataRepository extends JpaRepository<FavoritoEntity, String> {

    boolean existsByCompradorIdAndOfertaId(String compradorId, String ofertaId);

    long deleteByCompradorIdAndOfertaId(String compradorId, String ofertaId);

    List<FavoritoEntity> findByCompradorIdOrderByFechaDesc(String compradorId);
}