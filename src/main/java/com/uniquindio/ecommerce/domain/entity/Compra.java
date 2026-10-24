package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;

import java.time.LocalDateTime;
import java.util.Objects;

public class Compra {

    private final String id;
    private final String ofertaId;
    private final String compradorId;
    private final Precio precioCongelado;
    private final LocalDateTime fechaCompra;
    private EstadoCompra estado;

    private Compra(String id, String ofertaId, String compradorId, Precio precioCongelado, LocalDateTime fechaCompra, EstadoCompra estado) {
        this.id = id;
        this.ofertaId = ofertaId;
        this.compradorId = compradorId;
        this.precioCongelado = precioCongelado;
        this.fechaCompra = fechaCompra;
        this.estado = estado;
    }

    public static Compra realizar(String id, String ofertaId, String compradorId, Precio precioActual) {
        if (ofertaId == null || ofertaId.isBlank() || compradorId == null || compradorId.isBlank()) {
            throw new ReglaDominioException("La compra debe indicar la oferta y el comprador");
        }
        if (precioActual == null) {
            throw new ReglaDominioException("La compra debe incluir un precio válido");
        }
        return new Compra(id, ofertaId, compradorId, precioActual, LocalDateTime.now(), EstadoCompra.PENDIENTE);
    }

    public static Compra reconstruir(String id, String ofertaId, String compradorId, Precio precioCongelado, LocalDateTime fechaCompra, EstadoCompra estado) {
        return new Compra(id, ofertaId, compradorId, precioCongelado, fechaCompra, estado);
    }

    public void confirmar() {
        verificarQueNoEsteReembolsada();
        verificarTransicion(EstadoCompra.COMPLETADA);
        this.estado = EstadoCompra.COMPLETADA;
    }

    public void solicitarReembolso(String motivo) {
        verificarQueNoEsteReembolsada();
        if (this.estado != EstadoCompra.COMPLETADA) {
            throw new ReglaDominioException("Solo se puede reembolsar una compra completada");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("El reembolso exige un motivo");
        }
        verificarTransicion(EstadoCompra.REEMBOLSADA);
        this.estado = EstadoCompra.REEMBOLSADA;
    }

    private void verificarQueNoEsteReembolsada() {
        if (this.estado.esFinal()) {
            throw new ReglaDominioException("Una compra reembolsada no puede modificarse");
        }
    }

    private void verificarTransicion(EstadoCompra siguiente) {
        if (!this.estado.puedeTransicionarA(siguiente)) {
            throw new ReglaDominioException("No se puede cambiar de " + this.estado + " a " + siguiente);
        }
    }

    public String getId() { return id; }
    public String getOfertaId() { return ofertaId; }
    public String getCompradorId() { return compradorId; }
    public Precio getPrecioCongelado() { return precioCongelado; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public EstadoCompra getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compra otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}