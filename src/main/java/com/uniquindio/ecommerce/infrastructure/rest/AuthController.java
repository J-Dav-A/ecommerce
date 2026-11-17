
package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.infrastructure.rest.dto.LoginDTO;
import com.uniquindio.ecommerce.infrastructure.rest.dto.TokenDTO;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.ecommerce.infrastructure.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioJpaDataRepository usuarioRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            UsuarioJpaDataRepository usuarioRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        UsuarioEntity usuario = usuarioRepository
                .buscarPorEmail(dto.email())
                .orElseThrow();

        String token = jwtUtils.generarToken(
                usuario.getEmail(),
                usuario.getRol()
        );

        TokenDTO respuesta = new TokenDTO(
                token,
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getId(),
                usuario.getNombre()
        );

        return ResponseEntity.ok(respuesta);
    }
}
