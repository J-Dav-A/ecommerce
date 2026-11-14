package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.application.usecase.ConsultarMetricasDelVendedor.MetricaOferta;

public record MetricaOfertaResponse(
        String ofertaId,
        String titulo,
        long ventas,
        Double promedioCalificacion,
        long totalComentarios
) {
    public static MetricaOfertaResponse desde(MetricaOferta metrica) {
        Double promedio = metrica.promedioCalificacion() == null ? null
                : Math.round(metrica.promedioCalificacion() * 100) / 100.0;
        return new MetricaOfertaResponse(
                metrica.oferta().getId(),
                metrica.oferta().getTitulo(),
                metrica.ventas(),
                promedio,
                metrica.totalComentarios());
    }
}