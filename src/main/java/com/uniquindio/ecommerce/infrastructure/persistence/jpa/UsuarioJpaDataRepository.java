package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    default Optional<UsuarioEntity> buscarPorEmail(String email) {
        return findByEmail(email);
    }

    default boolean existePorEmail(String email) {
        return existsByEmail(email);
    }
}