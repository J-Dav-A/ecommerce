package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.valueobject.PoliticaContrasena;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CambiarContrasena {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void ejecutar(String usuarioId, String contrasenaActual, String contrasenaNueva) {
        Usuario usuario = usuarioRepository.obtenerPorId(usuarioId)
                .filter(Usuario::estaActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe"));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new ReglaDominioException("La contraseña actual no es correcta");
        }
        PoliticaContrasena.validar(contrasenaNueva);
        if (passwordEncoder.matches(contrasenaNueva, usuario.getContrasena())) {
            throw new ReglaDominioException("La contraseña nueva debe ser distinta de la actual");
        }

        usuario.cambiarContrasena(passwordEncoder.encode(contrasenaNueva));
        usuarioRepository.guardar(usuario);
    }
}