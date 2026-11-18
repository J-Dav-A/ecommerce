package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.FiltroBusqueda;
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

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class OfertaRepositoryJpa implements OfertaRepository {

    private static final BigDecimal PRECIO_TOPE = new BigDecimal("999999999");

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
    public List<Oferta> obtenerPorVendedorId(String vendedorId) {
        return dataRepository.findByVendedorIdAndEstadoNot(vendedorId, EstadoOferta.ELIMINADA).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Oferta> obtenerTodasPorVendedorId(String vendedorId) {
        return dataRepository.findByVendedorId(vendedorId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Pagina<Oferta> buscar(FiltroBusqueda filtro, int pagina, int tamano) {
        String patron = (filtro.texto() == null) ? "" : filtro.texto().trim();
        Set<Plataforma> plataformas = (filtro.plataforma() == null)
                ? EnumSet.allOf(Plataforma.class) : EnumSet.of(filtro.plataforma());
        // Sin filtro se usan valores que no excluyen nada (así la consulta nunca recibe parámetros nulos)
        BigDecimal precioMinimo = (filtro.precioMinimo() == null) ? BigDecimal.ZERO : filtro.precioMinimo();
        BigDecimal precioMaximo = (filtro.precioMaximo() == null) ? PRECIO_TOPE : filtro.precioMaximo();
        double calificacionMinima = (filtro.calificacionMinima() == null) ? 0.0 : filtro.calificacionMinima();

        Page<OfertaEntity> resultado = switch (filtro.orden()) {
            case POPULARIDAD -> dataRepository.buscarPorPopularidad(patron, plataformas, precioMinimo, precioMaximo,
                    calificacionMinima, PageRequest.of(pagina, tamano));
            case CALIFICACION -> dataRepository.buscarPorCalificacion(patron, plataformas, precioMinimo, precioMaximo,
                    calificacionMinima, PageRequest.of(pagina, tamano));
            case PRECIO_ASC -> dataRepository.buscar(patron, plataformas, precioMinimo, precioMaximo, calificacionMinima,
                    PageRequest.of(pagina, tamano, Sort.by("precio.monto").ascending().and(Sort.by("titulo"))));
            case PRECIO_DESC -> dataRepository.buscar(patron, plataformas, precioMinimo, precioMaximo, calificacionMinima,
                    PageRequest.of(pagina, tamano, Sort.by("precio.monto").descending().and(Sort.by("titulo"))));
            case NOMBRE -> dataRepository.buscar(patron, plataformas, precioMinimo, precioMaximo, calificacionMinima,
                    PageRequest.of(pagina, tamano, Sort.by("titulo")));
        };

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