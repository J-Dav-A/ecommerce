package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarVentasDelVendedor {

    public record Venta(Compra compra, Oferta oferta) {}

    private final OfertaRepository ofertaRepository;
    private final CompraRepository compraRepository;

    // Historial de compras hechas sobre las ofertas del vendedor (incluidas las ya eliminadas).
    // desde/hasta (inclusivos) y estado son filtros opcionales; el más reciente va primero.
    @Transactional(readOnly = true)
    public List<Venta> ejecutar(String vendedorId, LocalDate desde, LocalDate hasta, EstadoCompra estado) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaDominioException("La fecha inicial no puede ser posterior a la final");
        }
        return ofertaRepository.obtenerTodasPorVendedorId(vendedorId).stream()
                .flatMap(oferta -> compraRepository.obtenerPorOfertaId(oferta.getId()).stream()
                        .map(compra -> new Venta(compra, oferta)))
                .filter(v -> estado == null || v.compra().getEstado() == estado)
                .filter(v -> desde == null || !v.compra().getFechaCompra().isBefore(desde.atStartOfDay()))
                .filter(v -> hasta == null || v.compra().getFechaCompra().isBefore(hasta.plusDays(1).atStartOfDay()))
                .sorted(Comparator.comparing((Venta v) -> v.compra().getFechaCompra()).reversed())
                .toList();
    }
}