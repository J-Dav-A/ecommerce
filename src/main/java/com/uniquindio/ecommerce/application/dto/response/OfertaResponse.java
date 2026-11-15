package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;

import java.util.List;

// La primera imagen de la galería es la principal; promedioCalificacion es null si aún no hay comentarios
public record OfertaResponse(
        String id,
        String vendedorId,
        String titulo,
        double precioValor,
        String precioMoneda,
        int stock,
        String plataforma,
        String estado,
        List<String> imagenes,
        String imagenPrincipal,
        Double promedioCalificacion,
        long totalComentarios
) {
    public static OfertaResponse desde(Oferta oferta) {
        return desde(oferta, null);
    }

    public static OfertaResponse desde(Oferta oferta, ResumenPorOferta resumen) {
        List<String> imagenes = oferta.getImagenes();
        Double promedio = (resumen == null || resumen.promedio() == null) ? null
                : Math.round(resumen.promedio() * 100.0) / 100.0;
        return new OfertaResponse(
                oferta.getId(),
                oferta.getVendedorId(),
                oferta.getTitulo(),
                oferta.getPrecio().valor().doubleValue(),
                oferta.getPrecio().moneda(),
                oferta.getStock(),
                oferta.getPlataforma() == null ? null : oferta.getPlataforma().name(),
                oferta.getEstado().name(),
                imagenes,
                imagenes.isEmpty() ? null : imagenes.get(0),
                promedio,
                resumen == null || resumen.total() == null ? 0 : resumen.total()
        );
    }
}