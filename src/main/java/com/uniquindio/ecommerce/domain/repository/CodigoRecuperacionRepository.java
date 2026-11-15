package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;

import java.util.Optional;

public interface CodigoRecuperacionRepository {
    // Reemplaza el código anterior del mismo usuario
    void guardar(CodigoRecuperacion codigo);
    Optional<CodigoRecuperacion> obtenerPorUsuarioId(String usuarioId);
    void eliminar(String usuarioId);
}