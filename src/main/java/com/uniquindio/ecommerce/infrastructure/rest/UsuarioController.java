package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.CambiarContrasenaRequest;
import com.uniquindio.ecommerce.application.dto.request.EditarPerfilRequest;
import com.uniquindio.ecommerce.application.dto.request.RegistrarUsuarioRequest;
import com.uniquindio.ecommerce.application.dto.response.UsuarioResponse;
import com.uniquindio.ecommerce.application.usecase.CambiarContrasena;
import com.uniquindio.ecommerce.application.usecase.ConsultarPerfil;
import com.uniquindio.ecommerce.application.usecase.EditarPerfil;
import com.uniquindio.ecommerce.application.usecase.EliminarCuenta;
import com.uniquindio.ecommerce.application.usecase.RegistrarUsuario;
import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistrarUsuario registrarUsuario;
    private final ConsultarPerfil consultarPerfil;
    private final EditarPerfil editarPerfil;
    private final CambiarContrasena cambiarContrasena;
    private final EliminarCuenta eliminarCuenta;

    // Las rutas /me siempre trabajan sobre el usuario del token, nunca sobre un id enviado por el cliente
    @GetMapping("/me")
    public UsuarioResponse miPerfil(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return UsuarioResponse.desde(consultarPerfil.ejecutar(usuario.getId()));
    }

    @PutMapping("/me")
    public UsuarioResponse editarPerfil(@Valid @RequestBody EditarPerfilRequest request,
                                        @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return UsuarioResponse.desde(editarPerfil.ejecutar(
                usuario.getId(), request.nombre(), request.telefono(), request.fechaNacimiento()));
    }

    @PutMapping("/me/contrasena")
    public ResponseEntity<Void> cambiarContrasena(@Valid @RequestBody CambiarContrasenaRequest request,
                                                  @AuthenticationPrincipal UsuarioPrincipal usuario) {
        cambiarContrasena.ejecutar(usuario.getId(), request.contrasenaActual(), request.contrasenaNueva());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> eliminarCuenta(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        eliminarCuenta.ejecutar(usuario.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarUsuarioRequest request) {
        Usuario usuario = registrarUsuario.ejecutar(
                request.nombre(),
                request.email(),
                request.contrasena(),
                request.telefono(),
                request.rol(),
                request.fechaNacimiento());

        UsuarioResponse response = UsuarioResponse.desde(usuario);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}