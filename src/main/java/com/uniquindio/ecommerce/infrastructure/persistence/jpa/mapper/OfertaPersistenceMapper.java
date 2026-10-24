package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class OfertaPersistenceMapper {

    public OfertaEntity toEntity(Oferta oferta) {
        OfertaEntity entity = new OfertaEntity();
        entity.setId(oferta.getId());
        entity.setVendedorId(oferta.getVendedorId());
        entity.setTitulo(oferta.getTitulo());
        entity.setPrecio(new PrecioEmbeddable(oferta.getPrecio().valor(), oferta.getPrecio().moneda()));
        entity.setStock(oferta.getStock());
        entity.setPlataforma(oferta.getPlataforma());
        entity.setEstado(oferta.getEstado());
        return entity;
    }

    public Oferta toDomain(OfertaEntity entity) {
        return Oferta.reconstruir(
                entity.getId(),
                entity.getVendedorId(),
                entity.getTitulo(),
                new Precio(entity.getPrecio().getMonto(), entity.getPrecio().getMoneda()),
                entity.getStock(),
                entity.getPlataforma(),
                entity.getEstado()
        );
    }
}