
package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoJpaDataRepository
        extends JpaRepository<FavoritoEntity, String> {

    Optional<FavoritoEntity> findByCompradorIdAndOfertaId(
            String compradorId,
            String ofertaId
    );

    List<FavoritoEntity> findByCompradorId(String compradorId);

    void deleteByCompradorIdAndOfertaId(
            String compradorId,
            String ofertaId
    );
}
