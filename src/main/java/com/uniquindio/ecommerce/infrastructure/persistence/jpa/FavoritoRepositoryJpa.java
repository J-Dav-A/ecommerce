package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.FavoritoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FavoritoRepositoryJpa implements FavoritoRepository {

    private final FavoritoJpaDataRepository dataRepository;
    private final FavoritoPersistenceMapper mapper;

    @Override
    public boolean existe(String compradorId, String ofertaId) {
        return dataRepository.existsByCompradorIdAndOfertaId(compradorId, ofertaId);
    }

    @Override
    public void guardar(Favorito favorito) {
        dataRepository.save(mapper.toEntity(favorito));
    }

    @Override
    @Transactional
    public boolean eliminar(String compradorId, String ofertaId) {
        return dataRepository.deleteByCompradorIdAndOfertaId(compradorId, ofertaId) > 0;
    }

    @Override
    public List<Favorito> obtenerPorCompradorId(String compradorId) {
        return dataRepository.findByCompradorIdOrderByFechaDesc(compradorId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}