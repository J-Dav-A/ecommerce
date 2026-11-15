package com.uniquindio.ecommerce.domain.valueobject;

// Cómo se ordena el catálogo; NOMBRE es el orden por defecto
public enum OrdenOfertas {
    NOMBRE,
    PRECIO_ASC,
    PRECIO_DESC,
    POPULARIDAD,   // más ventas completadas primero
    CALIFICACION   // mejor calificación promedio primero
}