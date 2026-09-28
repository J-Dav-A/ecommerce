package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


public class Comentario {

    private final UUID id;
    private final UUID compraId;
    private final String contenido;
    private final int calificacion;
    private final LocalDateTime fechaCreacion;


    private Comentario(
            UUID id,
            UUID compraId,
            String contenido,
            int calificacion
    ) {
        this.id = id;
        this.compraId = compraId;
        this.contenido = contenido;
        this.calificacion = calificacion;
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Crea un comentario.
     *
     * Regla 1:
     * Todo comentario debe estar asociado a una compra.
     *
     * Regla 2:
     * El comentario debe tener contenido.
     *
     * Regla 3:
     * La calificación debe estar entre 1 y 5.
     */
    public static Comentario crear(
            UUID id,
            UUID compraId,
            String contenido,
            int calificacion
    ) {

        // Regla 1:
        // No se puede crear un comentario sin una compra asociada.
        if (id == null) {
            throw new ReglaDominioException(
                    "El comentario debe tener un identificador"
            );
        }

        if (compraId == null) {
            throw new ReglaDominioException(
                    "El comentario debe estar asociado a una compra"
            );
        }

        // Regla 2:
        // El comentario debe contener texto.
        if (contenido == null || contenido.isBlank()) {
            throw new ReglaDominioException(
                    "El comentario debe tener contenido"
            );
        }

        // Regla 3:
        // La calificación permitida está entre 1 y 5.
        if (calificacion < 1 || calificacion > 5) {
            throw new ReglaDominioException(
                    "La calificación debe estar entre 1 y 5"
            );
        }

        return new Comentario(
                id,
                compraId,
                contenido,
                calificacion
        );
    }

    // CONSULTAS

    public UUID getId() {
        return id;
    }

    public UUID getCompraId() {
        return compraId;
    }

    public String getContenido() {
        return contenido;
    }

    public int getCalificacion() {
        return calificacion;
    }

    /**
     * Regla 4:
     * La fecha de creación queda registrada al crear
     * el comentario y no puede modificarse.
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    // IDENTIDAD DE LA ENTIDAD

    /**
     * Dos comentarios representan la misma entidad
     * cuando tienen el mismo identificador.
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Comentario otro)) {
            return false;
        }

        return id.equals(otro.id);
    }

    /**
     * El hash se construye únicamente utilizando
     * el identificador de la entidad.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}