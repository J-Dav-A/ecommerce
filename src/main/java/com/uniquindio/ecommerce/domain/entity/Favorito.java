package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.time.LocalDateTime;
import java.util.Objects;

// Una oferta que un comprador guardó para verla después. Referencia a las otras entidades solo por id.
public class Favorito {

    private final String id;
    private final String compradorId;
    private final String ofertaId;
    private final LocalDateTime fecha;

    private Favorito(String id, String compradorId, String ofertaId, LocalDateTime fecha) {
        this.id = id;
        this.compradorId = compradorId;
        this.ofertaId = ofertaId;
        this.fecha = fecha;
    }

    public static Favorito crear(String id, String compradorId, String ofertaId, LocalDateTime fecha) {
        if (id == null || id.isBlank()) throw new ReglaDominioException("El favorito requiere identificador");
        if (compradorId == null || compradorId.isBlank()) throw new ReglaDominioException("El favorito requiere comprador");
        if (ofertaId == null || ofertaId.isBlank()) throw new ReglaDominioException("El favorito requiere oferta");
        if (fecha == null) throw new ReglaDominioException("El favorito requiere fecha");
        return new Favorito(id, compradorId, ofertaId, fecha);
    }

    public static Favorito reconstruir(String id, String compradorId, String ofertaId, LocalDateTime fecha) {
        return new Favorito(id, compradorId, ofertaId, fecha);
    }

    public String getId() { return id; }
    public String getCompradorId() { return compradorId; }
    public String getOfertaId() { return ofertaId; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorito otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}