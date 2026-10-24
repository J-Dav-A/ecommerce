package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaVentas;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.OfertaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Pagina<Oferta> buscar(String texto, Plataforma plataforma, int pagina, int tamano) {
        String patron = (texto == null) ? "" : texto.trim();
        Set<Plataforma> plataformas = (plataforma == null) ? EnumSet.allOf(Plataforma.class) : EnumSet.of(plataforma);

        Page<OfertaEntity> resultado = dataRepository.buscar(
                patron, plataformas, PageRequest.of(pagina, tamano, Sort.by("titulo")));

        return new Pagina<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize());
    }

    @Override
    public List<OfertaVentas> masVendidas(int limite) {
        return dataRepository.masVendidas(EstadoCompra.COMPLETADA, PageRequest.of(0, limite));
    }

    @Override
    public void guardar(Oferta oferta) {
        dataRepository.save(mapper.toEntity(oferta));
    }
}