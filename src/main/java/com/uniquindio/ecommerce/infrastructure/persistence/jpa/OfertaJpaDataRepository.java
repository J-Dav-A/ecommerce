package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.repository.OfertaVentas;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.OfertaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

interface OfertaJpaDataRepository extends JpaRepository<OfertaEntity, String> {

    List<OfertaEntity> findByEstado(EstadoOferta estado);

    @Query("""
        SELECT o FROM OfertaEntity o
        WHERE o.estado = com.uniquindio.ecommerce.domain.valueobject.EstadoOferta.PUBLICADA
        AND LOWER(o.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
        AND o.plataforma IN (:plataformas)
        """)
    Page<OfertaEntity> buscar(@Param("texto") String texto,
                              @Param("plataformas") Collection<Plataforma> plataformas,
                              Pageable pageable);

    @Query("""
        SELECT new com.uniquindio.ecommerce.domain.repository.OfertaVentas(o.id, o.titulo, COUNT(c))
        FROM OfertaEntity o
        JOIN CompraEntity c ON c.ofertaId = o.id
        WHERE c.estado = :estado
        GROUP BY o.id, o.titulo
        ORDER BY COUNT(c) DESC, o.titulo ASC
        """)
    List<OfertaVentas> masVendidas(@Param("estado") EstadoCompra estado, Pageable pageable);
}