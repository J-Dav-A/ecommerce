package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.repository.OfertaVentas;

public record OfertaVentaResponse(String ofertaId, String titulo, long ventas) {
    public static OfertaVentaResponse desde(OfertaVentas fila) {
        return new OfertaVentaResponse(fila.ofertaId(), fila.titulo(), fila.ventas());
    }
}