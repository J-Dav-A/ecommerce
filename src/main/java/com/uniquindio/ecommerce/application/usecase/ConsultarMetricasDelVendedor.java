package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarMetricasDelVendedor {

    public record MetricaOferta(Oferta oferta, long ventas, Double promedioCalificacion, long totalComentarios) {}

    private final OfertaRepository ofertaRepository;
    private final CompraRepository compraRepository;
    private final ComentarioRepository comentarioRepository;

    // desde y hasta son opcionales (ambos inclusive); sin ellos se cuenta todo el historial
    @Transactional(readOnly = true)
    public List<MetricaOferta> ejecutar(String vendedorId, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaDominioException("La fecha inicial no puede ser posterior a la final");
        }
        return ofertaRepository.obtenerPorVendedorId(vendedorId).stream()
                .map(oferta -> metricaDe(oferta, desde, hasta))
                .toList();
    }

    private MetricaOferta metricaDe(Oferta oferta, LocalDate desde, LocalDate hasta) {
        // Una venta es una compra completada (las reembolsadas y pendientes no cuentan)
        long ventas = compraRepository.obtenerPorOfertaId(oferta.getId()).stream()
                .filter(c -> c.estaCompletada() && enRango(c.getFechaCompra(), desde, hasta))
                .count();

        List<Comentario> comentarios = comentarioRepository.buscarPorOfertaId(oferta.getId()).stream()
                .filter(c -> enRango(c.getFecha(), desde, hasta))
                .toList();
        Double promedio = comentarios.isEmpty() ? null
                : comentarios.stream().mapToInt(Comentario::getCalificacion).average().orElse(0);

        return new MetricaOferta(oferta, ventas, promedio, comentarios.size());
    }

    private boolean enRango(LocalDateTime fecha, LocalDate desde, LocalDate hasta) {
        boolean despuesDeDesde = desde == null || !fecha.isBefore(desde.atStartOfDay());
        boolean antesDeHasta = hasta == null || fecha.isBefore(hasta.plusDays(1).atStartOfDay());
        return despuesDeDesde && antesDeHasta;
    }
}