package com.uniquindio.ecommerce.domain.repository;

// Promedio de calificaciones y cantidad de comentarios de una oferta
public record ResumenPorOferta(String ofertaId, Double promedio, Long total) {}