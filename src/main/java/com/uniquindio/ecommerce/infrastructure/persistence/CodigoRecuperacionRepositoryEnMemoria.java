package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;
import com.uniquindio.ecommerce.domain.repository.CodigoRecuperacionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CodigoRecuperacionRepositoryEnMemoria implements CodigoRecuperacionRepository {

    private final Map<String, CodigoRecuperacion> codigos = new ConcurrentHashMap<>();

    @Override
    public void guardar(CodigoRecuperacion codigo) {
        codigos.put(codigo.getUsuarioId(), codigo);
    }

    @Override
    public Optional<CodigoRecuperacion> obtenerPorUsuarioId(String usuarioId) {
        return Optional.ofNullable(codigos.get(usuarioId));
    }

    @Override
    public void eliminar(String usuarioId) {
        codigos.remove(usuarioId);
    }
}