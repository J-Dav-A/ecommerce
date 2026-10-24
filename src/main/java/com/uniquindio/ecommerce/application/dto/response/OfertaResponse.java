package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.Oferta;

public record OfertaResponse(
        String id,
        String vendedorId,
        String titulo,
        double precioValor,
        String precioMoneda,
        int stock,
        String plataforma,
        String estado
) {
    public static OfertaResponse desde(Oferta oferta) {
        return new OfertaResponse(
                oferta.getId(),
                oferta.getVendedorId(),
                oferta.getTitulo(),
                oferta.getPrecio().valor().doubleValue(),
                oferta.getPrecio().moneda(),
                oferta.getStock(),
                oferta.getPlataforma() == null ? null : oferta.getPlataforma().name(),
                oferta.getEstado().name()
        );
    }
}