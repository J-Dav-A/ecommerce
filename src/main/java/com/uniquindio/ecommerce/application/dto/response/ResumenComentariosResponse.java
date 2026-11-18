package com.uniquindio.ecommerce.application.dto.response;

import java.util.List;

// Comentarios de una oferta con su calificación promedio (0 si todavía no tiene comentarios)
public record ResumenComentariosResponse(
        double promedioCalificacion,
        int total,
        List<ComentarioResponse> comentarios
) {}