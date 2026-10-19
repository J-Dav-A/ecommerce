package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import java.util.Objects;

public class Comentario {

    private final String id;
    private final String compradorId;
    private final String ofertaId;
    private final String texto;

    private Comentario(String id, String compradorId, String ofertaId, String texto) {
        this.id = id;
        this.compradorId = compradorId;
        this.ofertaId = ofertaId;
        this.texto = texto;
    }

    public static Comentario crear(String id, String compradorId, String ofertaId, String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaDominioException("El comentario no puede estar vacío");
        }
        return new Comentario(id, compradorId, ofertaId, texto);
    }

    public static Comentario reconstruir(String id, String compradorId, String ofertaId, String texto) {
        return new Comentario(id, compradorId, ofertaId, texto);
    }

    public String getId() { return id; }
    public String getCompradorId() { return compradorId; }
    public String getOfertaId() { return ofertaId; }
    public String getTexto() { return texto; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comentario otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}