package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Comentario;

import java.time.LocalDateTime;

public record ComentarioResponse(
        String id,
        String compradorId,
        String ofertaId,
        String compraId,
        String texto,
        int calificacion,
        LocalDateTime fecha,
        String respuesta,
        LocalDateTime fechaRespuesta
) {
    public static ComentarioResponse desde(Comentario comentario) {
        return new ComentarioResponse(
                comentario.getId(),
                comentario.getCompradorId(),
                comentario.getOfertaId(),
                comentario.getCompraId(),
                comentario.getTexto(),
                comentario.getCalificacion(),
                comentario.getFecha(),
                comentario.getRespuesta(),
                comentario.getFechaRespuesta());
    }
}