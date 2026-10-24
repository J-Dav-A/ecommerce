package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.ClaveDigitalEntity;
import org.springframework.stereotype.Component;

@Component
public class ClaveDigitalPersistenceMapper {

    public ClaveDigitalEntity toEntity(ClaveDigital clave) {
        ClaveDigitalEntity entity = new ClaveDigitalEntity();
        entity.setId(clave.getId());
        entity.setOfertaId(clave.getOfertaId());
        entity.setCodigo(clave.getCodigo());
        entity.setMaxActivaciones(clave.getLicencia().maxActivaciones());
        entity.setTransferible(clave.getLicencia().transferible());
        entity.setEstado(clave.getEstado());
        entity.setCompradorId(clave.getCompradorId());
        entity.setFechaAsignacion(clave.getFechaAsignacion());
        return entity;
    }

    public ClaveDigital toDomain(ClaveDigitalEntity entity) {
        return ClaveDigital.reconstruir(
                entity.getId(),
                entity.getOfertaId(),
                entity.getCodigo(),
                new LicenciaDigital(entity.getMaxActivaciones(), entity.isTransferible()),
                entity.getEstado(),
                entity.getCompradorId(),
                entity.getFechaAsignacion()
        );
    }
}