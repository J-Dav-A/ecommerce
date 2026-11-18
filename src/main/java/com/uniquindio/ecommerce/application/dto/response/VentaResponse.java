package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.application.usecase.ConsultarVentasDelVendedor.Venta;

import java.time.LocalDateTime;

public record VentaResponse(
        String compraId,
        String ofertaId,
        String tituloOferta,
        String compradorId,
        double precioValor,
        String precioMoneda,
        LocalDateTime fecha,
        String estado
) {
    public static VentaResponse desde(Venta venta) {
        return new VentaResponse(
                venta.compra().getId(),
                venta.oferta().getId(),
                venta.oferta().getTitulo(),
                venta.compra().getCompradorId(),
                venta.compra().getPrecioCongelado().valor().doubleValue(),
                venta.compra().getPrecioCongelado().moneda(),
                venta.compra().getFechaCompra(),
                venta.compra().getEstado().name());
    }
}