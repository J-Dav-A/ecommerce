package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.ComentarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;

import java.util.Collection;
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
        return dataRepository.findByOfertaIdOrderByFechaDesc(ofertaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ResumenPorOferta> resumenPorOfertas(Collection<String> ofertaIds) {
        if (ofertaIds.isEmpty()) return List.of(); // IN () vacío no es válido en SQL
        return dataRepository.resumenPorOfertas(ofertaIds);
    }

    @Override
    public boolean existePorCompraId(String compraId) {
        return dataRepository.existsByCompraId(compraId);
    }

    @Override
    public void guardar(Comentario comentario) {
        dataRepository.save(mapper.toEntity(comentario));
    }
}