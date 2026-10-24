package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Comentario;

public record ComentarioResponse(String id, String compradorId, String ofertaId, String texto) {
    public static ComentarioResponse desde(Comentario comentario) {
        return new ComentarioResponse(
                comentario.getId(),
                comentario.getCompradorId(),
                comentario.getOfertaId(),
                comentario.getTexto());
    }
}