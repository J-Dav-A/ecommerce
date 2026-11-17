
package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.FavoritoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FavoritoRepositoryJpa implements FavoritoRepository {

    private final FavoritoJpaDataRepository dataRepository;
    private final FavoritoPersistenceMapper mapper;

    @Override
    public Optional<Favorito> obtenerPorCompradorYOferta(
            String compradorId,
            String ofertaId
    ) {
        return dataRepository
                .findByCompradorIdAndOfertaId(compradorId, ofertaId)
                .map(mapper::toDomain);
    }

    @Override
    public List<String> buscarOfertaIdsPorCompradorId(String compradorId) {
        return dataRepository.findByCompradorId(compradorId)
                .stream()
                .map(FavoritoEntity -> FavoritoEntity.getOfertaId())
                .toList();
    }

    @Override
    public void guardar(Favorito favorito) {
        dataRepository.save(mapper.toEntity(favorito));
    }

    @Override
    public void eliminar(String compradorId, String ofertaId) {
        dataRepository.deleteByCompradorIdAndOfertaId(compradorId, ofertaId);
    }
}
