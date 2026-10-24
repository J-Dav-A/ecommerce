package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class ClaveDigital {

    private final String id;
    private final String ofertaId;
    private final String codigo;
    private final LicenciaDigital licencia;
    private EstadoClaveDigital estado;
    private String compradorId;
    private Instant fechaAsignacion;

    private ClaveDigital(String id, String ofertaId, String codigo, LicenciaDigital licencia, EstadoClaveDigital estado, String compradorId, Instant fechaAsignacion) {
        this.id = id;
        this.ofertaId = ofertaId;
        this.codigo = codigo;
        this.licencia = licencia;
        this.estado = estado;
        this.compradorId = compradorId;
        this.fechaAsignacion = fechaAsignacion;
    }

    public static ClaveDigital crear(String id, String ofertaId, String codigo, LicenciaDigital licencia) {
        if (id == null || id.isBlank()) throw new ReglaDominioException("Id de clave obligatorio");
        if (ofertaId == null || ofertaId.isBlank()) throw new ReglaDominioException("Oferta requerida para la clave");
        if (codigo == null || codigo.isBlank()) throw new ReglaDominioException("El código no puede estar vacío");
        if (licencia == null) throw new ReglaDominioException("La licencia es obligatoria");

        return new ClaveDigital(id, ofertaId, codigo, licencia, EstadoClaveDigital.DISPONIBLE, null, null);
    }

    public static ClaveDigital reconstruir(String id, String ofertaId, String codigo, LicenciaDigital licencia, EstadoClaveDigital estado, String compradorId, Instant fechaAsignacion) {
        return new ClaveDigital(id, ofertaId, codigo, licencia, estado, compradorId, fechaAsignacion);
    }

    public void asignar(String compradorId, Instant fecha) {
        if (compradorId == null || compradorId.isBlank()) throw new ReglaDominioException("Comprador obligatorio");
        if (estado != EstadoClaveDigital.DISPONIBLE) throw new ReglaDominioException("La clave no está disponible");
        this.compradorId = compradorId;
        this.fechaAsignacion = fecha;
        this.estado = EstadoClaveDigital.ASIGNADA;
    }

    public void canjear() {
        if (estado != EstadoClaveDigital.ASIGNADA) throw new ReglaDominioException("Solo una clave asignada puede canjearse");
        this.estado = EstadoClaveDigital.CANJEADA;
    }

    public void reembolsar(Instant ahora, Duration plazo) {
        if (estado == EstadoClaveDigital.CANJEADA) throw new ReglaDominioException("No se reembolsan claves ya canjeadas");
        if (estado != EstadoClaveDigital.ASIGNADA) throw new ReglaDominioException("Solo se reembolsan claves asignadas");
        if (ahora.isAfter(fechaAsignacion.plus(plazo))) throw new ReglaDominioException("El plazo de reembolso expiró");
        this.estado = EstadoClaveDigital.REEMBOLSADA;
    }

    public String getId() { return id; }
    public String getOfertaId() { return ofertaId; }
    public String getCodigo() { return codigo; }
    public LicenciaDigital getLicencia() { return licencia; }
    public EstadoClaveDigital getEstado() { return estado; }
    public String getCompradorId() { return compradorId; }
    public Instant getFechaAsignacion() { return fechaAsignacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaveDigital otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}