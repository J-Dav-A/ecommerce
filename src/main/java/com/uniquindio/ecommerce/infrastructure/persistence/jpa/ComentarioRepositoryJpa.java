package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.ComentarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ComentarioRepositoryJpa implements ComentarioRepository {

    private final ComentarioJpaDataRepository dataRepository;
    private final ComentarioPersistenceMapper mapper;

    @Override
    public Optional<Comentario> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Comentario> buscarPorOfertaId(String ofertaId) {
        return dataRepository.findByOfertaId(ofertaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void guardar(Comentario comentario) {
        dataRepository.save(mapper.toEntity(comentario));
    }
}