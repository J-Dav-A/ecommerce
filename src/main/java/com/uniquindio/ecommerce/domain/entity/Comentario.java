package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comentario {

    public static final int MAX_CARACTERES = 500;

    private final String id;
    private final String compradorId;
    private final String ofertaId;
    private final String compraId;
    private final String texto;
    private final int calificacion;
    private final LocalDateTime fecha;
    private String respuesta;
    private LocalDateTime fechaRespuesta;

    private Comentario(String id, String compradorId, String ofertaId, String compraId, String texto,
                       int calificacion, LocalDateTime fecha, String respuesta, LocalDateTime fechaRespuesta) {
        this.id = id;
        this.compradorId = compradorId;
        this.ofertaId = ofertaId;
        this.compraId = compraId;
        this.texto = texto;
        this.calificacion = calificacion;
        this.fecha = fecha;
        this.respuesta = respuesta;
        this.fechaRespuesta = fechaRespuesta;
    }

    public static Comentario crear(String id, String compradorId, String ofertaId, String compraId,
                                   String texto, int calificacion, LocalDateTime fecha) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaDominioException("El comentario no puede estar vacío");
        }
        if (texto.length() > MAX_CARACTERES) {
            throw new ReglaDominioException("El comentario no puede superar " + MAX_CARACTERES + " caracteres");
        }
        if (calificacion < 1 || calificacion > 5) {
            throw new ReglaDominioException("La calificación debe estar entre 1 y 5");
        }
        return new Comentario(id, compradorId, ofertaId, compraId, texto, calificacion, fecha, null, null);
    }

    public static Comentario reconstruir(String id, String compradorId, String ofertaId, String compraId,
                                         String texto, int calificacion, LocalDateTime fecha,
                                         String respuesta, LocalDateTime fechaRespuesta) {
        return new Comentario(id, compradorId, ofertaId, compraId, texto, calificacion, fecha, respuesta, fechaRespuesta);
    }

    // El vendedor responde una sola vez a cada comentario
    public void responder(String respuesta, LocalDateTime fecha) {
        if (this.respuesta != null) {
            throw new ReglaDominioException("El comentario ya fue respondido");
        }
        if (respuesta == null || respuesta.isBlank()) {
            throw new ReglaDominioException("La respuesta no puede estar vacía");
        }
        if (respuesta.length() > MAX_CARACTERES) {
            throw new ReglaDominioException("La respuesta no puede superar " + MAX_CARACTERES + " caracteres");
        }
        this.respuesta = respuesta;
        this.fechaRespuesta = fecha;
    }

    public boolean estaRespondido() {
        return respuesta != null;
    }

    public String getId() { return id; }
    public String getCompradorId() { return compradorId; }
    public String getOfertaId() { return ofertaId; }
    public String getCompraId() { return compraId; }
    public String getTexto() { return texto; }
    public int getCalificacion() { return calificacion; }
    public LocalDateTime getFecha() { return fecha; }
    public String getRespuesta() { return respuesta; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }

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