package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;
import com.uniquindio.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.CodigoRecuperacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CodigoRecuperacionRepositoryJpa implements CodigoRecuperacionRepository {

    private final CodigoRecuperacionJpaDataRepository dataRepository;

    @Override
    public void guardar(CodigoRecuperacion codigo) {
        CodigoRecuperacionEntity entity = new CodigoRecuperacionEntity();
        entity.setUsuarioId(codigo.getUsuarioId());
        entity.setCodigoHash(codigo.getCodigoHash());
        entity.setExpiraEn(codigo.getExpiraEn());
        entity.setIntentos(codigo.getIntentos());
        dataRepository.save(entity); // misma clave = reemplaza el código anterior
    }

    @Override
    public Optional<CodigoRecuperacion> obtenerPorUsuarioId(String usuarioId) {
        return dataRepository.findById(usuarioId).map(e ->
                CodigoRecuperacion.reconstruir(e.getUsuarioId(), e.getCodigoHash(), e.getExpiraEn(), e.getIntentos()));
    }

    @Override
    public void eliminar(String usuarioId) {
        dataRepository.deleteById(usuarioId);
    }
}