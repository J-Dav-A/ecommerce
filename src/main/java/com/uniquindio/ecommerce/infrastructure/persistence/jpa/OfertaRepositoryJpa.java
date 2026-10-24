package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.OfertaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OfertaRepositoryJpa implements OfertaRepository {

    private final OfertaJpaDataRepository dataRepository;
    private final OfertaPersistenceMapper mapper;

    @Override
    public Optional<Oferta> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Oferta> obtenerPublicadas() {
        return dataRepository.findByEstado(EstadoOferta.PUBLICADA).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void guardar(Oferta oferta) {
        dataRepository.save(mapper.toEntity(oferta));
    }
}