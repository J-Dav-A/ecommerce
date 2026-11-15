package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.CodigoRecuperacion;
import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.service.EnviadorCorreo;
import com.uniquindio.ecommerce.domain.service.GeneradorDeCodigo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitarRecuperacionContrasena {

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnviadorCorreo enviadorCorreo;
    private final GeneradorDeCodigo generadorDeCodigo;

    // No devuelve nada ni falla si el correo no existe: así nadie puede averiguar qué correos están registrados
    @Transactional
    public void ejecutar(String email) {
        usuarioRepository.obtenerPorEmail(email)
                .filter(Usuario::estaActivo)
                .ifPresent(this::enviarCodigo);
    }

    private void enviarCodigo(Usuario usuario) {
        String codigo = generadorDeCodigo.nuevo();
        codigoRepository.guardar(CodigoRecuperacion.crear(
                usuario.getId(), passwordEncoder.encode(codigo), LocalDateTime.now()));
        try {
            enviadorCorreo.enviar(usuario.getEmail(), "Código para recuperar tu contraseña",
                    "Hola " + usuario.getNombre() + ",\n\nTu código de recuperación es: " + codigo
                            + "\nVence en " + CodigoRecuperacion.MINUTOS_DE_VALIDEZ + " minutos.\n\n"
                            + "Si no lo pediste tú, ignora este mensaje.");
        } catch (RuntimeException e) {
            // Si el correo falla no se revela: la respuesta es la misma. Queda registrado para el equipo.
            log.warn("No se pudo enviar el código de recuperación: {}", e.getMessage());
        }
    }
}