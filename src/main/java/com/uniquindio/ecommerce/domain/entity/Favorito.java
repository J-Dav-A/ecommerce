
package com.uniquindio.ecommerce.domain.entity;

import java.util.Objects;

public class Favorito {

    private final String id;
    private final String compradorId;
    private final String ofertaId;

    private Favorito(String id, String compradorId, String ofertaId) {
        this.id = id;
        this.compradorId = compradorId;
        this.ofertaId = ofertaId;
    }

    public static Favorito crear(String id, String compradorId, String ofertaId) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El favorito requiere un identificador");
        }
        if (compradorId == null || compradorId.isBlank()) {
            throw new IllegalArgumentException("El favorito requiere un comprador");
        }
        if (ofertaId == null || ofertaId.isBlank()) {
            throw new IllegalArgumentException("El favorito requiere una oferta");
        }

        return new Favorito(id, compradorId, ofertaId);
    }

    public static Favorito reconstruir(String id, String compradorId, String ofertaId) {
        return new Favorito(id, compradorId, ofertaId);
    }

    public String getId() {
        return id;
    }

    public String getCompradorId() {
        return compradorId;
    }

    public String getOfertaId() {
        return ofertaId;
    }

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
