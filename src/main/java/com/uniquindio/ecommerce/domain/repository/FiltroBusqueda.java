package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.valueobject.OrdenOfertas;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;

import java.math.BigDecimal;

// Filtros opcionales del catálogo; cualquier campo null significa "sin filtro" (el orden null es por nombre)
public record FiltroBusqueda(
        String texto,
        Plataforma plataforma,
        BigDecimal precioMinimo,
        BigDecimal precioMaximo,
        Double calificacionMinima,
        OrdenOfertas orden
) {
    public FiltroBusqueda {
        if (orden == null) orden = OrdenOfertas.NOMBRE;
    }

    // Sin orden explícito (se ordena por nombre)
    public FiltroBusqueda(String texto, Plataforma plataforma, BigDecimal precioMinimo,
                          BigDecimal precioMaximo, Double calificacionMinima) {
        this(texto, plataforma, precioMinimo, precioMaximo, calificacionMinima, OrdenOfertas.NOMBRE);
    }
}