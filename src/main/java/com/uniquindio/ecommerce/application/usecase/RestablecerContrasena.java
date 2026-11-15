package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;
import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.valueobject.PoliticaContrasena;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RestablecerContrasena {

    private static final String CODIGO_INVALIDO = "El código no es válido o expiró";

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordEncoder passwordEncoder;

    // noRollbackFor: el intento fallido debe quedar guardado aunque se lance la excepción; si no, el
    // límite de 5 intentos nunca se alcanzaría
    @Transactional(noRollbackFor = ReglaDominioException.class)
    public void ejecutar(String email, String codigo, String contrasenaNueva) {
        // Todos los fallos dan el mismo mensaje para no revelar si el correo existe
        Usuario usuario = usuarioRepository.obtenerPorEmail(email)
                .filter(Usuario::estaActivo)
                .orElseThrow(() -> new ReglaDominioException(CODIGO_INVALIDO));
        CodigoRecuperacion guardado = codigoRepository.obtenerPorUsuarioId(usuario.getId())
                .orElseThrow(() -> new ReglaDominioException(CODIGO_INVALIDO));

        if (!guardado.estaVigente(LocalDateTime.now())) {
            codigoRepository.eliminar(usuario.getId());
            throw new ReglaDominioException(CODIGO_INVALIDO);
        }
        if (!passwordEncoder.matches(codigo, guardado.getCodigoHash())) {
            guardado.registrarIntentoFallido();
            codigoRepository.guardar(guardado);
            throw new ReglaDominioException(CODIGO_INVALIDO);
        }

        PoliticaContrasena.validar(contrasenaNueva);
        usuario.cambiarContrasena(passwordEncoder.encode(contrasenaNueva));
        usuarioRepository.guardar(usuario);
        codigoRepository.eliminar(usuario.getId()); // un solo uso
    }
}