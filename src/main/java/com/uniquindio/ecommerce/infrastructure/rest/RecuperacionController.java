package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.RecuperarContrasenaRequest;
import com.uniquindio.ecommerce.application.dto.request.RestablecerContrasenaRequest;
import com.uniquindio.ecommerce.application.usecase.RestablecerContrasena;
import com.uniquindio.ecommerce.application.usecase.SolicitarRecuperacionContrasena;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Públicos (/api/auth/**): el usuario no puede iniciar sesión porque olvidó su contraseña
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RecuperacionController {

    private final SolicitarRecuperacionContrasena solicitarRecuperacion;
    private final RestablecerContrasena restablecerContrasena;

    // Siempre responde igual, exista o no el correo
    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, String>> recuperar(@Valid @RequestBody RecuperarContrasenaRequest request) {
        solicitarRecuperacion.ejecutar(request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("mensaje", "Si el correo está registrado, te enviamos un código de 6 dígitos"));
    }

    @PostMapping("/restablecer")
    public ResponseEntity<Void> restablecer(@Valid @RequestBody RestablecerContrasenaRequest request) {
        restablecerContrasena.ejecutar(request.email(), request.codigo(), request.contrasenaNueva());
        return ResponseEntity.noContent().build();
    }
}