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
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OfertaJpaDataRepository extends JpaRepository<OfertaEntity, String> {

    List<OfertaEntity> findByEstado(EstadoOferta estado);

    // Consulta JPQL dinámica con filtros opcionales de texto y plataforma
    @Query("SELECT o FROM OfertaEntity o WHERE " +
            "(:texto IS NULL OR :texto = '' OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))) AND " +
            "(:plataformas IS NULL OR o.plataforma IN :plataformas) AND " +
            "o.estado = com.uniquindio.ecommerce.domain.valueobject.EstadoOferta.PUBLICADA")
    Page<OfertaEntity> buscar(@Param("texto") String texto,
                              @Param("plataformas") Set<Plataforma> plataformas,
                              Pageable pageable);

    // Consulta JPQL avanzada con JOIN y GROUP BY para calcular las más vendidas
    @Query("SELECT c.oferta AS oferta, COUNT(c) AS cantidadVentas " +
            "FROM CompraEntity c " +
            "WHERE c.estado = :estado " +
            "GROUP BY c.oferta " +
            "ORDER BY COUNT(c) DESC")
    List<OfertaVentas> masVendidas(@Param("estado") EstadoCompra estado, Pageable pageable);
}