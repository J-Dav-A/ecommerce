package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.UUID;

public class RegistrarUsuario {

    private final UsuarioRepository usuarioRepository;

    public RegistrarUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario ejecutar(
            String nombre,
            String email,
            String contraseña,
            String telefono,
            String rol,
            LocalDate fechaNacimiento
    ) {

        if (email == null || email.isBlank()) {
            throw new ReglaDominioException("Debe indicarse el email del usuario.");
        }

        /*
         * Regla de negocio:
         * El email de cada usuario debe ser único.
         */
        if (usuarioRepository.existePorEmail(email)) {
            throw new ReglaDominioException(
                    "Ya existe un usuario registrado con ese email."
            );
        }

        Usuario usuario = Usuario.crear(
                UUID.randomUUID(),
                nombre,
                email,
                contraseña,
                telefono,
                rol,
                fechaNacimiento
        );

        usuarioRepository.guardar(usuario);

        return usuario;
    }
}