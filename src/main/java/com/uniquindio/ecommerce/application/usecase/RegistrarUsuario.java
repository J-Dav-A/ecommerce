package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarUsuario {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario ejecutar(String nombre, String email, String contrasena, String telefono, String rol, LocalDate fechaNacimiento) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new ReglaDominioException("El email ya se encuentra registrado");
        }
        String contrasenaCifrada = passwordEncoder.encode(contrasena);
        Usuario usuario = Usuario.crear(UUID.randomUUID().toString(), nombre, email, contrasenaCifrada, telefono, rol, fechaNacimiento);
        usuarioRepository.guardar(usuario);
        return usuario;
    }
}