package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.valueobject.Plataforma;

import java.math.BigDecimal;

// Filtros opcionales del catálogo; cualquier campo null significa "sin filtro"
public record FiltroBusqueda(
        String texto,
        Plataforma plataforma,
        BigDecimal precioMinimo,
        BigDecimal precioMaximo,
        Double calificacionMinima
) {}