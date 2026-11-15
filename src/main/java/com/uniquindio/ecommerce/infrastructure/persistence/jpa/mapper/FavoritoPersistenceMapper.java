package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.FavoritoEntity;
import org.springframework.stereotype.Component;

@Component
public class FavoritoPersistenceMapper {

    public FavoritoEntity toEntity(Favorito favorito) {
        FavoritoEntity entity = new FavoritoEntity();
        entity.setId(favorito.getId());
        entity.setCompradorId(favorito.getCompradorId());
        entity.setOfertaId(favorito.getOfertaId());
        entity.setFecha(favorito.getFecha());
        return entity;
    }

    public Favorito toDomain(FavoritoEntity entity) {
        return Favorito.reconstruir(entity.getId(), entity.getCompradorId(), entity.getOfertaId(), entity.getFecha());
    }
}