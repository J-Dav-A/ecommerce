package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad Comentario.
 *
 * Reglas de negocio:
 * Regla 4: Un comprador no puede calificar un producto sin haberlo comprado.
 *
 * Otras validaciones propias de la entidad:
 * - Todo comentario debe estar asociado a una compra.
 * - El comentario debe tener contenido.
 * - La calificación debe estar entre 1 y 5.
 * - Todo comentario debe registrar su fecha de creación.
 */
public class Comentario {

    private final UUID id;
    private final String compraId;
    private final String contenido;
    private final int calificacion;
    private final LocalDateTime fechaCreacion;

    private Comentario(
            UUID id,
            String compraId,
            String contenido,
            int calificacion
    ) {
        this.id = id;
        this.compraId = compraId;
        this.contenido = contenido;
        this.calificacion = calificacion;
        this.fechaCreacion = LocalDateTime.now();
    }

    public static Comentario crear(
            UUID id,
            String compraId,
            String contenido,
            int calificacion
    ) {

        if (id == null) {
            throw new ReglaDominioException(
                    "El comentario debe tener un identificador"
            );
        }

        if (compraId == null || compraId.isBlank()) {
            throw new ReglaDominioException(
                    "El comentario debe estar asociado a una compra"
            );
        }

        if (contenido == null || contenido.isBlank()) {
            throw new ReglaDominioException(
                    "El comentario debe tener contenido"
            );
        }

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

    public UUID getId() {
        return id;
    }

    public String getCompraId() {
        return compraId;
    }

    public String getContenido() {
        return contenido;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}