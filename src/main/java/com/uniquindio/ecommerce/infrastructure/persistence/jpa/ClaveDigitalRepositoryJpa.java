package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.ClaveDigitalPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClaveDigitalRepositoryJpa implements ClaveDigitalRepository {

    private final ClaveDigitalJpaDataRepository dataRepository;
    private final ClaveDigitalPersistenceMapper mapper;

    @Override
    public Optional<ClaveDigital> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ClaveDigital> obtenerDisponiblePorOferta(String ofertaId) {
        return dataRepository.findFirstByOfertaIdAndEstado(ofertaId, EstadoClaveDigital.DISPONIBLE)
                .map(mapper::toDomain);
    }

    @Override
    public void guardar(ClaveDigital clave) {
        dataRepository.save(mapper.toEntity(clave));
    }
}