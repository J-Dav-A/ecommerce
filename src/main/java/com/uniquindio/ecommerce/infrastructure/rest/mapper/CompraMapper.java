package com.uniquindio.ecommerce.infrastructure.rest.mapper;

import com.uniquindio.ecommerce.application.dto.response.CompraDetalleResponse;
import com.uniquindio.ecommerce.domain.entity.Compra;
import org.springframework.stereotype.Component;

@Component
public class CompraMapper {

    public CompraDetalleResponse toDetalleResponse(Compra compra) {
        return new CompraDetalleResponse(
                compra.getId(),
                compra.getOfertaId(),
                compra.getCompradorId(),
                compra.getPrecioCongelado().valor(),
                compra.getPrecioCongelado().moneda(),
                compra.getEstado().toString(),
                compra.getFechaCompra()
        );
    }
}