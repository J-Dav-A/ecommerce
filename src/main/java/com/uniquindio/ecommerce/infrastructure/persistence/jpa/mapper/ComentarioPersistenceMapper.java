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
        entity.setCompraId(comentario.getCompraId());
        entity.setTexto(comentario.getTexto());
        entity.setCalificacion(comentario.getCalificacion());
        entity.setFecha(comentario.getFecha());
        entity.setRespuesta(comentario.getRespuesta());
        entity.setFechaRespuesta(comentario.getFechaRespuesta());
        return entity;
    }

    public Comentario toDomain(ComentarioEntity entity) {
        return Comentario.reconstruir(
                entity.getId(),
                entity.getCompradorId(),
                entity.getOfertaId(),
                entity.getCompraId(),
                entity.getTexto(),
                entity.getCalificacion(),
                entity.getFecha(),
                entity.getRespuesta(),
                entity.getFechaRespuesta()
        );
    }
}