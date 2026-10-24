package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ComentarioEntity;
import org.springframework.stereotype.Component;

@Component
public class ComentarioPersistenceMapper {

    public ComentarioEntity toEntity(Comentario comentario) {
        ComentarioEntity entity = new ComentarioEntity();
        entity.setId(comentario.getId());
        entity.setCompradorId(comentario.getCompradorId());
        entity.setOfertaId(comentario.getOfertaId());
        entity.setTexto(comentario.getTexto());
        return entity;
    }

    public Comentario toDomain(ComentarioEntity entity) {
        return Comentario.reconstruir(
                entity.getId(),
                entity.getCompradorId(),
                entity.getOfertaId(),
                entity.getTexto()
        );
    }
}