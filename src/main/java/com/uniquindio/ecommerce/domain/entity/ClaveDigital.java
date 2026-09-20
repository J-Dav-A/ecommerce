package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ClaveDigital {

    private final UUID id;
    private final String codigo;
    private final LicenciaDigital licencia;
    private EstadoClaveDigital estado;
    private UUID compradorId;
    private Instant fechaAsignacion;

    public ClaveDigital(UUID id, String codigo, LicenciaDigital licencia) {
        if (id == null) {
            throw new ReglaDominioException("La clave digital requiere un identificador.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new ReglaDominioException("El código de la clave no puede estar vacío.");
        }
        if (licencia == null) {
            throw new ReglaDominioException("La clave debe tener una licencia.");
        }
        this.id = id;
        this.codigo = codigo;
        this.licencia = licencia;
        this.estado = EstadoClaveDigital.DISPONIBLE;
    }

    /** regla 2 solo una clave DISPONIBLE puede asignarse y una sola vez */
    public void asignar(UUID compradorId, Instant ahora) {
        if (compradorId == null) {
            throw new ReglaDominioException("Debe indicarse el comprador");
        }
        if (estado != EstadoClaveDigital.DISPONIBLE) {
            throw new ReglaDominioException("La clave ya fue asignada y no puede volver a comercializarse");
        }
        this.compradorId = compradorId;
        this.fechaAsignacion = ahora;
        this.estado = EstadoClaveDigital.ASIGNADA;
    }

    public void canjear() {
        if (estado != EstadoClaveDigital.ASIGNADA) {
            throw new ReglaDominioException("Solo una clave asignada puede canjearse");
        }
        this.estado = EstadoClaveDigital.CANJEADA;
    }

    /** regla 3 no hay reembolso de claves canjeadas ni fuera del plazo */
    public void reembolsar(Instant ahora, Duration plazo) {
        if (estado == EstadoClaveDigital.CANJEADA) {
            throw new ReglaDominioException("No se reembolsan claves ya canjeadas.");
        }
        if (estado != EstadoClaveDigital.ASIGNADA) {
            throw new ReglaDominioException("Solo una clave asignada puede reembolsarse.");
        }
        if (ahora.isAfter(fechaAsignacion.plus(plazo))) {
            throw new ReglaDominioException("El plazo de reembolso ya venció.");
        }
        this.estado = EstadoClaveDigital.REEMBOLSADA;
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public LicenciaDigital getLicencia() { return licencia; }
    public EstadoClaveDigital getEstado() { return estado; }
    public UUID getCompradorId() { return compradorId; }
    public Instant getFechaAsignacion() { return fechaAsignacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaveDigital other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
