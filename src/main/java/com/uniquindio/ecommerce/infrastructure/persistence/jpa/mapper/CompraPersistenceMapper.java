package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.CompraEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class CompraPersistenceMapper {

    public CompraEntity toEntity(Compra compra) {
        CompraEntity entity = new CompraEntity();
        entity.setId(compra.getId());
        entity.setCompradorId(compra.getCompradorId());
        entity.setOfertaId(compra.getOfertaId());
        entity.setPrecio(new PrecioEmbeddable(compra.getPrecioCongelado().valor(), compra.getPrecioCongelado().moneda()));
        entity.setEstado(compra.getEstado());
        entity.setFechaCompra(compra.getFechaCompra());
        return entity;
    }

    public Compra toDomain(CompraEntity entity) {
        return Compra.reconstruir(
                entity.getId(),
                entity.getOfertaId(),
                entity.getCompradorId(),
                new Precio(entity.getPrecio().getMonto(), entity.getPrecio().getMoneda()),
                entity.getFechaCompra(),
                entity.getEstado()
        );
    }
}