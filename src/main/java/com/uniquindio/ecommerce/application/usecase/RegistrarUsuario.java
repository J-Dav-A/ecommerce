package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarUsuario {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario ejecutar(String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        Usuario usuario = Usuario.crear(UUID.randomUUID().toString(), nombre, email, contrasena, telefono, rol, fechaNacimiento);
        usuarioRepository.guardar(usuario);
        return usuario;
    }
}