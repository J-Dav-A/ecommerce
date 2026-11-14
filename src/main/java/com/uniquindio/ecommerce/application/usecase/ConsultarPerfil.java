package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsultarPerfil {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Usuario ejecutar(String usuarioId) {
        return usuarioRepository.obtenerPorId(usuarioId)
                .filter(Usuario::estaActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe"));
    }
}