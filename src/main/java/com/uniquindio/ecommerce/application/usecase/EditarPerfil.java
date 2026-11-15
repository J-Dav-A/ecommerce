package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditarPerfil {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario ejecutar(String usuarioId, String nombre, String telefono, LocalDate fechaNacimiento) {
        Usuario usuario = usuarioRepository.obtenerPorId(usuarioId)
                .filter(Usuario::estaActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe"));
        usuario.editarPerfil(nombre, telefono, fechaNacimiento);
        usuarioRepository.guardar(usuario);
        return usuario;
    }

    // Reemplaza también la foto, la descripción y los datos de pago (null o vacío los borra)
    @Transactional
    public Usuario ejecutar(String usuarioId, String nombre, String telefono, LocalDate fechaNacimiento,
                            String fotoPerfil, String descripcion, String datosPago) {
        Usuario usuario = usuarioRepository.obtenerPorId(usuarioId)
                .filter(Usuario::estaActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe"));
        usuario.editarPerfil(nombre, telefono, fechaNacimiento);
        usuario.definirPerfilExtendido(fotoPerfil, descripcion, datosPago);
        usuarioRepository.guardar(usuario);
        return usuario;
    }
}