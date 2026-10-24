package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, String> {
    boolean existsByEmail(String email);
}