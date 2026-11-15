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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface OfertaJpaDataRepository extends JpaRepository<OfertaEntity, String> {

    // Filtros comunes del catálogo (texto, plataforma, rango de precio y calificación mínima) solo sobre ofertas publicadas
    String DESDE_FILTRADAS = "FROM OfertaEntity o WHERE " +
            "(:texto IS NULL OR :texto = '' OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))) AND " +
            "(:plataformas IS NULL OR o.plataforma IN :plataformas) AND " +
            "o.precio.monto >= :precioMinimo AND o.precio.monto <= :precioMaximo AND " +
            "COALESCE((SELECT AVG(m.calificacion) FROM ComentarioEntity m WHERE m.ofertaId = o.id), 0.0) >= :calificacionMinima AND " +
            "o.estado = com.uniquindio.ecommerce.domain.valueobject.EstadoOferta.PUBLICADA";

    List<OfertaEntity> findByEstado(EstadoOferta estado);

    List<OfertaEntity> findByVendedorId(String vendedorId);

    List<OfertaEntity> findByVendedorIdAndEstadoNot(String vendedorId, EstadoOferta estado);

    // El orden (nombre o precio) llega en el Pageable
    @Query(value = "SELECT o " + DESDE_FILTRADAS, countQuery = "SELECT COUNT(o) " + DESDE_FILTRADAS)
    Page<OfertaEntity> buscar(@Param("texto") String texto,
                              @Param("plataformas") Set<Plataforma> plataformas,
                              @Param("precioMinimo") BigDecimal precioMinimo,
                              @Param("precioMaximo") BigDecimal precioMaximo,
                              @Param("calificacionMinima") double calificacionMinima,
                              Pageable pageable);

    @Query(value = "SELECT o " + DESDE_FILTRADAS + " ORDER BY " +
            "(SELECT COUNT(c) FROM CompraEntity c WHERE c.ofertaId = o.id AND " +
            "c.estado = com.uniquindio.ecommerce.domain.valueobject.EstadoCompra.COMPLETADA) DESC, o.titulo ASC",
            countQuery = "SELECT COUNT(o) " + DESDE_FILTRADAS)
    Page<OfertaEntity> buscarPorPopularidad(@Param("texto") String texto,
                                            @Param("plataformas") Set<Plataforma> plataformas,
                                            @Param("precioMinimo") BigDecimal precioMinimo,
                                            @Param("precioMaximo") BigDecimal precioMaximo,
                                            @Param("calificacionMinima") double calificacionMinima,
                                            Pageable pageable);

    @Query(value = "SELECT o " + DESDE_FILTRADAS + " ORDER BY " +
            "COALESCE((SELECT AVG(m2.calificacion) FROM ComentarioEntity m2 WHERE m2.ofertaId = o.id), 0.0) DESC, o.titulo ASC",
            countQuery = "SELECT COUNT(o) " + DESDE_FILTRADAS)
    Page<OfertaEntity> buscarPorCalificacion(@Param("texto") String texto,
                                             @Param("plataformas") Set<Plataforma> plataformas,
                                             @Param("precioMinimo") BigDecimal precioMinimo,
                                             @Param("precioMaximo") BigDecimal precioMaximo,
                                             @Param("calificacionMinima") double calificacionMinima,
                                             Pageable pageable);

    // Consulta JPQL avanzada con JOIN y GROUP BY para calcular las más vendidas
    @Query("SELECT new com.uniquindio.ecommerce.domain.repository.OfertaVentas(o.id, o.titulo, COUNT(c)) " +
            "FROM OfertaEntity o JOIN CompraEntity c ON c.ofertaId = o.id " +
            "WHERE c.estado = :estado " +
            "GROUP BY o.id, o.titulo " +
            "ORDER BY COUNT(c) DESC, o.titulo ASC")
    List<OfertaVentas> masVendidas(@Param("estado") EstadoCompra estado, Pageable pageable);
}