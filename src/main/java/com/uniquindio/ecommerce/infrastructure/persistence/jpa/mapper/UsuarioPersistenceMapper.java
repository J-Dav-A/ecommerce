package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceMapper {

    public UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setContrasena(usuario.getContrasena());
        entity.setTelefono(usuario.getTelefono());
        entity.setRol(usuario.getRol());
        entity.setFechaNacimiento(usuario.getFechaNacimiento());
        return entity;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        return Usuario.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContrasena(),
                entity.getTelefono(),
                entity.getRol(),
                entity.getFechaNacimiento()
        );
    }
}